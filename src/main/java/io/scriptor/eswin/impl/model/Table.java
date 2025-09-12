package io.scriptor.eswin.impl.model;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class Table {

    public interface ColumnGenerator {

        @NotNull Column from(final @NotNull Table table, final @NotNull String name);
    }

    private final Schema schema;
    private final String name;
    private final Set<Column> columns = new HashSet<>();
    private PrimaryKey primaryKey;
    private final Set<ForeignKey> foreignKeys = new HashSet<>();
    private final Set<UniqueConstraint> uniqueConstraints = new HashSet<>();

    public Table(final @NotNull Schema schema, final @NotNull String name) {
        this.schema = schema;
        this.name = name;
    }

    public @NotNull Column getColumn(final @NotNull String name, final @NotNull ColumnGenerator generator) {
        final var opt = columns.stream()
                               .filter(column -> column.getName().equals(name))
                               .findAny();
        if (opt.isPresent())
            return opt.get();
        final var column = generator.from(this, name);
        columns.add(column);
        return column;
    }

    public @NotNull UniqueConstraint getUniqueConstraint(
            final @NotNull String name,
            final @NotNull Supplier<UniqueConstraint> supplier
    ) {
        final var opt = uniqueConstraints.stream()
                                         .filter(constraint -> constraint.getName().equals(name))
                                         .findAny();
        if (opt.isPresent())
            return opt.get();
        final var uniqueConstraint = supplier.get();
        uniqueConstraints.add(uniqueConstraint);
        return uniqueConstraint;
    }

    public @NotNull Schema getSchema() {
        return schema;
    }

    public @NotNull String getName() {
        return name;
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

    public boolean hasPrimaryKey() {
        return primaryKey != null;
    }

    public @NotNull PrimaryKey getPrimaryKey() {
        if (primaryKey == null)
            throw new IllegalStateException();
        return primaryKey;
    }

    public void setPrimaryKey(final @NotNull PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public @NotNull Stream<ForeignKey> getForeignKeys() {
        return foreignKeys.stream();
    }

    public void addForeignKey(final @NotNull ForeignKey foreignKey) {
        foreignKeys.add(foreignKey);
    }

    public void removeForeignKey(final @NotNull ForeignKey foreignKey) {
        foreignKeys.remove(foreignKey);
    }

    public @NotNull Stream<UniqueConstraint> getUniqueConstraints() {
        return uniqueConstraints.stream();
    }

    public void addUniqueConstraint(final @NotNull UniqueConstraint uniqueConstraint) {
        uniqueConstraints.add(uniqueConstraint);
    }

    public void removeUniqueConstraint(final @NotNull UniqueConstraint uniqueConstraint) {
        uniqueConstraints.remove(uniqueConstraint);
    }

    @Override
    public @NotNull String toString() {
        return "%s.%s".formatted(schema, name);
    }
}
