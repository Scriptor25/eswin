package io.scriptor.eswin.impl.db;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

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

    public @NotNull String name() {
        return name;
    }

    public @NotNull Table table() {
        return table;
    }

    public @NotNull Set<Column> columns() {
        return columns;
    }

    public @NotNull String filterCondition() {
        return filterCondition;
    }
}
