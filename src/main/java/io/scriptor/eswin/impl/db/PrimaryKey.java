package io.scriptor.eswin.impl.db;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public final class PrimaryKey {

    private final String name;
    private final Table table;
    private final Set<Column> columns = new HashSet<>();

    public PrimaryKey(
            final @NotNull String name,
            final @NotNull Table table
    ) {
        this.name = name;
        this.table = table;
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull Table table() {
        return table;
    }

    public @NotNull Set<Column> columns() {
        return columns;
    }
}
