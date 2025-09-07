package io.scriptor.eswin.impl.db;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public final class ForeignKey {
    private final String name;
    private final Table pkTable;
    private final Set<Column> pkColumns = new HashSet<>();
    private final Table fkTable;
    private final Set<Column> fkColumns = new HashSet<>();
    private final ConstraintRule updateRule;
    private final ConstraintRule deleteRule;
    private final Deferrability deferrability;

    public ForeignKey(
            final @NotNull String name,
            final @NotNull Table pkTable,
            final @NotNull Table fkTable,
            final @NotNull ConstraintRule updateRule,
            final @NotNull ConstraintRule deleteRule,
            final @NotNull Deferrability deferrability
    ) {
        this.name = name;
        this.pkTable = pkTable;
        this.fkTable = fkTable;
        this.updateRule = updateRule;
        this.deleteRule = deleteRule;
        this.deferrability = deferrability;
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull Table pkTable() {
        return pkTable;
    }

    public @NotNull Set<Column> pkColumns() {
        return pkColumns;
    }

    public @NotNull Table fkTable() {
        return fkTable;
    }

    public @NotNull Set<Column> fkColumns() {
        return fkColumns;
    }

    public @NotNull ConstraintRule updateRule() {
        return updateRule;
    }

    public @NotNull ConstraintRule deleteRule() {
        return deleteRule;
    }

    public @NotNull Deferrability deferrability() {
        return deferrability;
    }
}
