package io.scriptor.eswin.impl.db;

import org.jetbrains.annotations.NotNull;

public class DatabaseRef {

    private final Server server;
    private final String name;

    public DatabaseRef(final @NotNull Server server, final @NotNull String name) {
        this.server = server;
        this.name = name;
    }

    public @NotNull Server server() {
        return server;
    }

    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull String toString() {
        return name;
    }
}
