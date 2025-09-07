package io.scriptor.eswin.impl.db;

import org.jetbrains.annotations.NotNull;

public class SchemaRef {

    private final Database database;
    private final String name;

    public SchemaRef(final @NotNull Database database, final @NotNull String name) {
        this.database = database;
        this.name = name;
    }

    public @NotNull Database database() {
        return database;
    }

    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull String toString() {
        return "%s.%s".formatted(database, name);
    }
}
