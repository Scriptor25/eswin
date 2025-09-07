package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.Context;
import io.scriptor.eswin.impl.db.DatabaseRef;
import io.scriptor.eswin.impl.db.SchemaRef;
import io.scriptor.eswin.impl.db.ServerRef;
import io.scriptor.eswin.impl.db.Table;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

public interface SourceContext extends Context {

    @NotNull Connection connection();

    @NotNull Stream<ServerRef> servers();

    @NotNull Stream<DatabaseRef> databases();

    @NotNull Stream<SchemaRef> schemas();

    @NotNull Stream<Table> tables();

    boolean selectServer(int index) throws SQLException;

    boolean createServer(
            @NotNull String label,
            @NotNull String url,
            @NotNull String username,
            @NotNull String password
    ) throws SQLException;

    boolean selectDatabase(@NotNull String databaseName) throws SQLException;

    boolean createDatabase(@NotNull String name) throws SQLException;

    boolean selectSchema(@NotNull String schemaName) throws SQLException;

    boolean createSchema(@NotNull String name) throws SQLException;
}
