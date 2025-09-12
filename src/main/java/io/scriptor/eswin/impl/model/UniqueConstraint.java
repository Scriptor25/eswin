package io.scriptor.eswin.impl.model;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class UniqueConstraint {

    private final String name;
    private final Table table;
    private final Set<Column> columns = new HashSet<>();
    private final String filterCondition;

    public UniqueConstraint(
            final @NotNull String name,
            final @NotNull Table table,
            final @NotNull String filterCondition
    ) {
        this.name = name;
        this.table = table;
        this.filterCondition = filterCondition;
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

    public void removeColumn(final @NotNull Column column) {
        columns.remove(column);
    }

    public @NotNull String getFilterCondition() {
        return filterCondition;
    }
}
