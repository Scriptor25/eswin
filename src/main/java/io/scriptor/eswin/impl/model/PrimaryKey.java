package io.scriptor.eswin.impl.model;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

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

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Table getTable() {
        return table;
    }

    public @NotNull Stream<Column> getColumns() {
        return columns.stream();
    }

    public void addColumn(final @NotNull Column column) {
        columns.add(column);
    }
}
