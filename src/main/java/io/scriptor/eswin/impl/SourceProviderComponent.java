package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import io.scriptor.eswin.component.context.ContextProvider.ContextFrame;
import io.scriptor.eswin.impl.model.*;
import io.scriptor.eswin.util.Log;
import io.scriptor.eswin.util.RefMap;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component("source-provider")
public class SourceProviderComponent extends ComponentBase {

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

    private final RefMap<Schema, SchemaRef> schemaRefs = new RefMap<>();
    private final Map<Schema, Map<Table, Map<String, ForeignKey>>> fkMap = new HashMap<>();

    private final SourceContext context = new SourceContext() {

        @Override
        public @NotNull Connection getConnection() {
            if (connection == null)
                throw new IllegalStateException();
            return connection;
        }

        @Override
        public @NotNull Stream<ServerRef> getServers() {
            return servers.stream();
        }

        @Override
        public @NotNull Stream<DatabaseRef> getDatabases() {
            return server.getDatabases();
        }

        @Override
        public @NotNull Stream<SchemaRef> getSchemas() {
            return database.getSchemas();
        }

        @Override
        public @NotNull Stream<Table> getTables() {
            return schemaRefs.stream().flatMap(Schema::getTables);
        }

        @Override
        public void selectServer(final int index) throws SQLException {
            if (index < 0 || index >= servers.size())
                throw new IndexOutOfBoundsException("server index %d out of range [0;%d)"
                                                            .formatted(index, servers.size()));

            final var ref = servers.get(index);

            server = new Server(ref);

            database = null;
            schema = null;
            schemaRefs.clear();
            fkMap.clear();

            if (connection != null)
                connection.close();

            connection = DriverManager.getConnection(ref.url(), ref.username(), ref.password());

            Log.info("server '%s'", ref.label());

            final var metadata = connection.getMetaData();
            try (final var databaseSet = metadata.getCatalogs()) {
                while (databaseSet.next()) {
                    final var databaseName = databaseSet.getString("TABLE_CAT");
                    server.addDatabase(new DatabaseRef(server, databaseName));

                    Log.info(" - database '%s'", databaseName);
                }
            }
        }

        @Override
        public void createServer(
                @NotNull String label,
                @NotNull String url,
                final @NotNull String username,
                final @NotNull String password
        ) throws SQLException {
            label = label.trim();
            if (label.isEmpty())
                throw new IllegalArgumentException("server label must not be empty or blank");

            url = url.trim();
            if (url.isEmpty())
                throw new IllegalArgumentException("server url must not be empty or blank");

            if (!url.endsWith("/"))
                url += '/';

            final var index = servers.size();
            servers.add(new ServerRef(label, url, username, password));

            selectServer(index);
        }

        @Override
        public void selectDatabase(final @NotNull String databaseName) throws SQLException {
            if (server == null)
                throw new IllegalStateException();

            final var ref = server.getDatabase(databaseName);

            database = new Database(ref);

            schema = null;
            schemaRefs.clear();
            fkMap.clear();

            if (connection != null)
                connection.close();

            connection = DriverManager.getConnection(server.getURL() + database.getName(),
                                                     server.getUsername(),
                                                     server.getPassword());

            Log.info("database '%s'", database.getName());

            initializeDatabase();

            final var metadata = connection.getMetaData();
            try (final var schemaSet = metadata.getSchemas(database.getName(), null)) {
                while (schemaSet.next()) {
                    final var schemaName = schemaSet.getString("TABLE_SCHEM");
                    database.addSchema(new SchemaRef(database, schemaName));

                    Log.info(" - schema '%s'", schemaName);
                }
            }
        }

        @Override
        public void createDatabase(@NotNull String databaseName) throws SQLException {
            databaseName = databaseName.trim().toLowerCase();
            if (databaseName.isEmpty())
                throw new IllegalArgumentException("database name must not be empty or blank");
            if (!databaseName.matches("[a-z0-9_]+"))
                throw new IllegalArgumentException("database name must only contain letters, digits or underscore");

            if (server == null)
                throw new IllegalStateException();
            if (connection == null || connection.isClosed())
                throw new IllegalStateException();

            try (final var s = connection.createStatement()) {
                s.executeUpdate("CREATE DATABASE %s".formatted(databaseName));
            }

            server.addDatabase(new DatabaseRef(server, databaseName));

            selectDatabase(databaseName);
        }

        @Override
        public void selectSchema(final @NotNull String schemaName) throws SQLException {
            if (database == null)
                throw new IllegalStateException();
            if (connection == null || connection.isClosed())
                throw new IllegalStateException();

            final var ref = database.getSchema(schemaName);

            schema = schemaRefs.get(ref, Schema::new);
            fkMap.clear();

            Log.info("schema '%s'", schema.getName());

            final var metadata = connection.getMetaData();
            try (final var tablesSet = metadata.getTables(database.getName(),
                                                          schema.getName(),
                                                          "%",
                                                          new String[] { "TABLE" })) {
                while (tablesSet.next()) {
                    final var tableName = tablesSet.getString("TABLE_NAME");

                    final var table = schema.getTable(tableName, Table::new);

                    Log.info(" - table '%s'", tableName);

                    try (final var columns = metadata.getColumns(database.getName(),
                                                                 schema.getName(),
                                                                 table.getName(),
                                                                 "%")) {
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

                            final var column = table.getColumn(columnName, Column::new);

                            column.setType(ColumnType.from(dataType))
                                  .setColumnSize(columnSize)
                                  .setDecimalDigits(decimalDigits)
                                  .setNumPrecisionRadix(numPrecisionRadix)
                                  .setRemarks(remarks)
                                  .setColumnDefault(columnDef)
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

                    try (final var keys = metadata.getPrimaryKeys(database.getName(), schema.getName(), tableName)) {
                        if (keys.next()) {
                            final var keyName    = keys.getString("PK_NAME");
                            final var primaryKey = new PrimaryKey(keyName, table);

                            do {
                                final var columnName = keys.getString("COLUMN_NAME");
                                final var column     = table.getColumn(columnName, Column::new);

                                primaryKey.addColumn(column);
                            } while (keys.next());

                            table.setPrimaryKey(primaryKey);
                        }
                    }

                    try (final var keys = metadata.getImportedKeys(database.getName(),
                                                                   schema.getName(),
                                                                   table.getName())) {
                        getForeignKeys(table, keys);
                    }

                    try (final var constraints = metadata.getIndexInfo(database.getName(),
                                                                       schema.getName(),
                                                                       table.getName(),
                                                                       true,
                                                                       false)) {
                        while (constraints.next()) {
                            final var filterCondition = constraints.getString("FILTER_CONDITION");

                            final var constraintName = constraints.getString("INDEX_NAME");
                            final var constraint = table.getUniqueConstraint(constraintName, () -> (
                                    new UniqueConstraint(constraintName, table, filterCondition)
                            ));

                            final var columnName = constraints.getString("COLUMN_NAME");
                            final var column     = table.getColumn(columnName, Column::new);

                            constraint.addColumn(column);
                        }
                    }
                }
            }
        }

        @Override
        public void createSchema(@NotNull String schemaName) throws SQLException {
            schemaName = schemaName.trim().toLowerCase();
            if (schemaName.isEmpty())
                throw new IllegalArgumentException("schema name must not be empty or blank");
            if (!schemaName.matches("[a-z0-9_]+"))
                throw new IllegalArgumentException("schema name must only contain letters, digits or underscore");

            if (database == null)
                throw new IllegalStateException();
            if (connection == null || connection.isClosed())
                throw new IllegalStateException();

            try (final var s = connection.createStatement()) {
                s.executeUpdate("CREATE SCHEMA %s".formatted(schemaName));
            }

            database.addSchema(new SchemaRef(database, schemaName));

            selectSchema(schemaName);
        }
    };

    private void getForeignKeys(final @NotNull Table table, final @NotNull ResultSet keys) throws SQLException {
        while (keys.next()) {
            final var name = keys.getString("FK_NAME");

            final var pkTableSchemaName = keys.getString("PKTABLE_SCHEM");
            final var pkTableName       = keys.getString("PKTABLE_NAME");
            final var pkColumnName      = keys.getString("PKCOLUMN_NAME");

            final var pkTableSchema = schemaRefs.get(database.getSchema(pkTableSchemaName), Schema::new);
            final var pkTable       = pkTableSchema.getTable(pkTableName, Table::new);
            final var pkColumn      = pkTable.getColumn(pkColumnName, Column::new);

            final var fkTableSchemaName = keys.getString("FKTABLE_SCHEM");
            final var fkTableName       = keys.getString("FKTABLE_NAME");
            final var fkColumnName      = keys.getString("FKCOLUMN_NAME");

            final var fkTableSchema = schemaRefs.get(database.getSchema(fkTableSchemaName), Schema::new);
            final var fkTable       = fkTableSchema.getTable(fkTableName, Table::new);
            final var fkColumn      = fkTable.getColumn(fkColumnName, Column::new);

            final var updateRule    = ConstraintRule.of(keys.getShort("UPDATE_RULE"));
            final var deleteRule    = ConstraintRule.of(keys.getShort("DELETE_RULE"));
            final var deferrability = Deferrability.of(keys.getShort("DEFERRABILITY"));

            final var key = fkMap.computeIfAbsent(table.getSchema(), _ -> new HashMap<>())
                                 .computeIfAbsent(table, _ -> new HashMap<>())
                                 .computeIfAbsent(name, _ -> (
                                         new ForeignKey(name,
                                                        pkTable,
                                                        fkTable,
                                                        updateRule,
                                                        deleteRule,
                                                        deferrability)
                                 ));

            key.addPKColumn(pkColumn);
            key.addFKColumn(fkColumn);

            table.addForeignKey(key);
        }
    }


    private void initializeDatabase() throws SQLException {
        try (final var s = connection.createStatement()) {
            s.executeUpdate(
                    """
                    create schema if not exists eswin;
                    create table if not exists eswin.tables (
                        schema_name varchar not null,
                        table_name varchar not null,
                        position_x int not null,
                        position_y int not null,
                        primary key (schema_name, table_name)
                    );
                    """
            );
        }
    }

    private ContextFrame frame;

    public SourceProviderComponent(final @NotNull ComponentInfo info) {
        super(info);

        // TODO: load existing server configurations from application data
        // TODO: ---> implement application data
    }

    @Override
    protected void onDetached() {
        super.onDetached();

        if (connection != null) {
            try {
                connection.close();
            } catch (final SQLException e) {
                Log.warn("while closing connection: %s", e);
            }
        }
    }

    @Override
    protected void onBeginFrame() {
        frame = getProvider().provide(SourceContext.class, context);
    }

    @Override
    protected void onEndFrame() {
        frame.close();
    }
}
