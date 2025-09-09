package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import io.scriptor.eswin.component.context.ContextProvider;
import io.scriptor.eswin.impl.db.*;
import io.scriptor.eswin.util.Log;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

@Component("source-provider")
public class SourceProviderComponent extends ComponentBase {

    private static @NotNull String importedKey(final short value) {
        return switch (value) {
            case DatabaseMetaData.importedKeyCascade -> "cascade";
            case DatabaseMetaData.importedKeyRestrict -> "restrict";
            case DatabaseMetaData.importedKeySetNull -> "set null";
            case DatabaseMetaData.importedKeyNoAction -> "no action";
            case DatabaseMetaData.importedKeySetDefault -> "set default";
            case DatabaseMetaData.importedKeyInitiallyDeferred -> "initially deferred";
            case DatabaseMetaData.importedKeyInitiallyImmediate -> "initially immediate";
            case DatabaseMetaData.importedKeyNotDeferrable -> "not deferrable";
            default -> throw new IllegalStateException();
        };
    }

    private final List<ServerRef> servers = new ArrayList<>(List.of(
            new ServerRef("docker",
                          "jdbc:postgresql://localhost:5432/",
                          "postgres",
                          "12345678")
    ));

    private Server server;
    private Database database;
    private Schema schema;

    private Connection connection;

    private final Map<DatabaseRef, Database> databaseRefs = new HashMap<>();
    private final Map<SchemaRef, Schema> schemaRefs = new HashMap<>();

    private final Map<Database, Map<Schema, Map<Table, Map<String, ForeignKey>>>> foreignKeys = new HashMap<>();

    private final SourceContext context = new SourceContext() {

        @Override
        public @NotNull Connection connection() {
            if (connection == null)
                throw new IllegalStateException();
            return connection;
        }

        @Override
        public @NotNull Stream<ServerRef> servers() {
            return servers.stream();
        }

        @Override
        public @NotNull Stream<DatabaseRef> databases() {
            return server.databases().stream();
        }

        @Override
        public @NotNull Stream<SchemaRef> schemas() {
            return database.schemas().stream();
        }

        @Override
        public @NotNull Stream<Table> tables() {
            return schema.tables().stream();
        }

        @Override
        public boolean selectServer(final int index) throws SQLException {
            if (index < 0 || index >= servers.size())
                return false;

            if (connection != null)
                connection.close();

            final var ref = servers.get(index);

            server = new Server(ref);
            database = null;
            schema = null;

            connection = DriverManager.getConnection(ref.url(), ref.username(), ref.password());

            Log.info("server '%s'", ref.label());

            final var metadata = connection.getMetaData();
            try (final var databaseSet = metadata.getCatalogs()) {
                while (databaseSet.next()) {
                    final var databaseName = databaseSet.getString("TABLE_CAT");
                    server.databases().add(new DatabaseRef(server, databaseName));

                    Log.info(" - database '%s'", databaseName);
                }
            }

            return true;
        }

        @Override
        public boolean createServer(
                @NotNull String label,
                @NotNull String url,
                final @NotNull String username,
                final @NotNull String password
        ) throws SQLException {
            label = label.trim();
            if (label.isEmpty())
                return false;

            url = url.trim();
            if (url.isEmpty())
                return false;

            if (!url.endsWith("/"))
                url += '/';

            final var index = servers.size();
            servers.add(new ServerRef(label, url, username, password));

            return selectServer(index);
        }

        @Override
        public boolean selectDatabase(final @NotNull String databaseName) throws SQLException {
            connection.close();

            final var ref = server.database(databaseName);

            database = databaseRefs.computeIfAbsent(ref, Database::new);
            schema = null;

            connection = DriverManager.getConnection(server.url() + database.name(),
                                                     server.username(),
                                                     server.password());

            Log.info("database '%s'", database.name());

            final var metadata = connection.getMetaData();
            try (final var schemaSet = metadata.getSchemas(database.name(), null)) {
                while (schemaSet.next()) {
                    final var schemaName = schemaSet.getString("TABLE_SCHEM");
                    database.schemas().add(new SchemaRef(database, schemaName));

                    Log.info(" - schema '%s'", schemaName);
                }
            }

            return true;
        }

        @Override
        public boolean createDatabase(@NotNull String databaseName) throws SQLException {
            databaseName = databaseName.trim().toLowerCase();
            if (databaseName.isEmpty())
                return false;
            if (!databaseName.matches("[a-z0-9_]+"))
                return false;

            try (final var s = connection.createStatement()) {
                s.executeUpdate("CREATE DATABASE %s".formatted(databaseName));
            }

            server.databases().add(new DatabaseRef(server, databaseName));

            return selectDatabase(databaseName);
        }

        @Override
        public boolean selectSchema(final @NotNull String schemaName) throws SQLException {
            final var ref = database.schema(schemaName);

            schema = schemaRefs.computeIfAbsent(ref, Schema::new);

            Log.info("schema '%s'", schema.name());

            final var metadata = connection.getMetaData();
            try (final var tablesSet = metadata.getTables(database.name(),
                                                          schema.name(),
                                                          "%",
                                                          new String[] { "TABLE" })) {
                while (tablesSet.next()) {
                    final var tableName = tablesSet.getString("TABLE_NAME");

                    final var table       = schema.table(tableName, Table::new);
                    final var pkReference = table.primaryKey();

                    Log.info(" - table '%s'", tableName);

                    try (final var columns = metadata.getColumns(database.name(), schema.name(), table.name(), "%")) {
                        while (columns.next()) {
                            final var columnName        = columns.getString("COLUMN_NAME");
                            final var dataType          = columns.getInt("DATA_TYPE");
                            final var columnSize        = columns.getInt("COLUMN_SIZE");
                            final var decimalDigits     = columns.getInt("DECIMAL_DIGITS");
                            final var numPrecisionRadix = columns.getInt("NUM_PREC_RADIX");
                            final var remarks           = columns.getString("REMARKS");
                            final var columnDef         = columns.getString("COLUMN_DEF");
                            final var sqlDataType       = columns.getInt("SQL_DATA_TYPE");
                            final var sqlDatetimeSub    = columns.getInt("SQL_DATETIME_SUB");
                            final var charOctetLength   = columns.getInt("CHAR_OCTET_LENGTH");
                            final var ordinalPosition   = columns.getInt("ORDINAL_POSITION");
                            final var scopeCatalog      = columns.getString("SCOPE_CATALOG");
                            final var scopeSchema       = columns.getString("SCOPE_SCHEMA");
                            final var scopeTable        = columns.getString("SCOPE_TABLE");
                            final var sourceDataType    = columns.getShort("SOURCE_DATA_TYPE");
                            final var isNullable        = columns.getString("IS_NULLABLE");
                            final var isAutoIncrement   = columns.getString("IS_AUTOINCREMENT");
                            final var isGeneratedColumn = columns.getString("IS_GENERATEDCOLUMN");

                            final var column = table.column(columnName, Column::new);

                            column.setType(ColumnType.from(dataType))
                                  .setColumnSize(columnSize)
                                  .setDecimalDigits(decimalDigits)
                                  .setNumPrecisionRadix(numPrecisionRadix)
                                  .setRemarks(remarks)
                                  .setColumnDef(columnDef)
                                  .setSqlDataType(sqlDataType)
                                  .setSqlDatetimeSub(sqlDatetimeSub)
                                  .setCharOctetLength(charOctetLength)
                                  .setOrdinalPosition(ordinalPosition)
                                  .setNullable(isNullable)
                                  .setScopeCatalog(scopeCatalog)
                                  .setScopeSchema(scopeSchema)
                                  .setScopeTable(scopeTable)
                                  .setSourceDataType(sourceDataType)
                                  .setAutoIncrement(isAutoIncrement)
                                  .setGeneratedColumn(isGeneratedColumn);

                            Log.info("    - column '%s'", columnName);
                        }
                    }

                    try (final var keys = metadata.getPrimaryKeys(database.name(), schema.name(), tableName)) {
                        if (keys.next()) {
                            final var keyName = keys.getString("PK_NAME");
                            final var key     = new PrimaryKey(keyName, table);

                            do {
                                final var columnName = keys.getString("COLUMN_NAME");
                                final var column     = table.column(columnName);

                                key.columns().add(column);
                            } while (keys.next());

                            pkReference.set(key);
                        }
                    }

                    try (final var keys = metadata.getImportedKeys(database.name(), schema.name(), table.name())) {
                        getForeignKeys(table, keys, table.importedKeys());
                    }

                    try (final var keys = metadata.getExportedKeys(database.name(), schema.name(), table.name())) {
                        getForeignKeys(table, keys, table.exportedKeys());
                    }

                    try (final var constraints = metadata.getIndexInfo(database.name(),
                                                                       schema.name(),
                                                                       table.name(),
                                                                       true,
                                                                       false)) {
                        while (constraints.next()) {
                            final var filterCondition = constraints.getString("FILTER_CONDITION");

                            final var constraintName = constraints.getString("INDEX_NAME");
                            final var constraint = table.uniqueConstraint(constraintName, () -> (
                                    new UniqueConstraint(constraintName, table, filterCondition)
                            ));

                            final var columnName = constraints.getString("COLUMN_NAME");
                            final var column     = table.column(columnName);

                            constraint.columns().add(column);
                        }
                    }
                }
            }

            return true;
        }

        @Override
        public boolean createSchema(@NotNull String schemaName) throws SQLException {
            schemaName = schemaName.trim().toLowerCase();
            if (schemaName.isEmpty())
                return false;
            if (!schemaName.matches("[a-z0-9_]+"))
                return false;

            try (final var s = connection.createStatement()) {
                s.executeUpdate("CREATE SCHEMA %s".formatted(schemaName));
            }

            database.schemas().add(new SchemaRef(database, schemaName));

            return selectSchema(schemaName);
        }
    };

    private void getForeignKeys(
            final @NotNull Table table,
            final @NotNull ResultSet keys,
            final @NotNull Set<ForeignKey> destination
    ) throws SQLException {
        while (keys.next()) {
            final var name = keys.getString("FK_NAME");

            final var pkTableSchemaName = keys.getString("PKTABLE_SCHEM");
            final var pkTableName       = keys.getString("PKTABLE_NAME");
            final var pkColumnName      = keys.getString("PKCOLUMN_NAME");

            final var pkTableSchema = schemaRefs.computeIfAbsent(
                    table.schema()
                         .database()
                         .schema(pkTableSchemaName),
                    Schema::new);
            final var pkTable  = pkTableSchema.table(pkTableName, Table::new);
            final var pkColumn = pkTable.column(pkColumnName, Column::new);

            final var fkTableSchemaName = keys.getString("FKTABLE_SCHEM");
            final var fkTableName       = keys.getString("FKTABLE_NAME");
            final var fkColumnName      = keys.getString("FKCOLUMN_NAME");

            final var fkTableSchema = schemaRefs.computeIfAbsent(
                    table.schema()
                         .database()
                         .schema(fkTableSchemaName),
                    Schema::new);
            final var fkTable  = fkTableSchema.table(fkTableName, Table::new);
            final var fkColumn = fkTable.column(fkColumnName, Column::new);

            final var updateRule    = ConstraintRule.of(keys.getShort("UPDATE_RULE"));
            final var deleteRule    = ConstraintRule.of(keys.getShort("DELETE_RULE"));
            final var deferrability = Deferrability.of(keys.getShort("DEFERRABILITY"));

            final var key = foreignKeys.computeIfAbsent(table.schema().database(), _ -> new HashMap<>())
                                       .computeIfAbsent(table.schema(), _ -> new HashMap<>())
                                       .computeIfAbsent(table, _ -> new HashMap<>())
                                       .computeIfAbsent(name, _ -> (
                                               new ForeignKey(name,
                                                              pkTable,
                                                              fkTable,
                                                              updateRule,
                                                              deleteRule,
                                                              deferrability)
                                       ));

            key.pkColumns().add(pkColumn);
            key.fkColumns().add(fkColumn);

            destination.add(key);
        }
    }

    private ContextProvider.ContextFrame frame;

    public SourceProviderComponent(final @NotNull ComponentInfo info) {
        super(info);

        // TODO: load existing server configurations from application data
        // TODO: ---> implement application data
    }

    @Override
    protected void onBeginFrame() {
        frame = getProvider().provide(SourceContext.class, context);
    }

    @Override
    protected void onEndFrame() {
        frame.close();
    }

    @Override
    public void attach(final @NotNull Container container, boolean constraint) {
        getChildren().forEach(child -> child.attach(container, constraint));

        onAttached();
    }

    @Override
    public boolean attached() {
        return getChildren().allMatch(ComponentBase::attached);
    }

    @Override
    public @NotNull Container detach() {
        return getChildren()
                .map(ComponentBase::detach)
                .distinct()
                .findAny()
                .orElseThrow();
    }
}
