package io.scriptor.eswin.impl.model;

import org.jetbrains.annotations.NotNull;

public class DatabaseRef {

    private final Server server;
    private final String name;

    public DatabaseRef(final @NotNull Server server, final @NotNull String name) {
        this.server = server;
        this.name = name;
    }

    public @NotNull Server getServer() {
        return server;
    }

    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String toString() {
        return name;
    }
}
