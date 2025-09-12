package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.context.Context;
import io.scriptor.eswin.impl.model.DatabaseRef;
import io.scriptor.eswin.impl.model.SchemaRef;
import io.scriptor.eswin.impl.model.ServerRef;
import io.scriptor.eswin.impl.model.Table;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

public interface SourceContext extends Context {

    @NotNull Connection getConnection();

    @NotNull Stream<ServerRef> getServers();

    @NotNull Stream<DatabaseRef> getDatabases();

    @NotNull Stream<SchemaRef> getSchemas();

    @NotNull Stream<Table> getTables();

    void selectServer(int index) throws SQLException;

    void createServer(
            @NotNull String label,
            @NotNull String url,
            @NotNull String username,
            @NotNull String password
    ) throws SQLException;

    void selectDatabase(@NotNull String databaseName) throws SQLException;

    void createDatabase(@NotNull String name) throws SQLException;

    void selectSchema(@NotNull String schemaName) throws SQLException;

    void createSchema(@NotNull String name) throws SQLException;
}
