package io.scriptor.eswin.impl.model;

import org.jetbrains.annotations.NotNull;

public class SchemaRef {

    private final Database database;
    private final String name;

    public SchemaRef(final @NotNull Database database, final @NotNull String name) {
        this.database = database;
        this.name = name;
    }

    public @NotNull Database getDatabase() {
        return database;
    }

    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String toString() {
        return "%s:%s".formatted(database, name);
    }
}
