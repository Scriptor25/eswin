package io.scriptor.eswin.impl.model;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

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

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Table getPKTable() {
        return pkTable;
    }

    public @NotNull Stream<Column> getPKColumns() {
        return pkColumns.stream();
    }

    public void addPKColumn(final @NotNull Column column) {
        pkColumns.add(column);
    }

    public void removePKColumn(final @NotNull Column column) {
        pkColumns.remove(column);
    }

    public @NotNull Table getFKTable() {
        return fkTable;
    }

    public @NotNull Stream<Column> getFKColumns() {
        return fkColumns.stream();
    }

    public void addFKColumn(final @NotNull Column column) {
        fkColumns.add(column);
    }

    public void removeFKColumn(final @NotNull Column column) {
        fkColumns.remove(column);
    }

    public @NotNull ConstraintRule getUpdateRule() {
        return updateRule;
    }

    public @NotNull ConstraintRule getDeleteRule() {
        return deleteRule;
    }

    public @NotNull Deferrability getDeferrability() {
        return deferrability;
    }
}
