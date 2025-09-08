package io.scriptor.eswin.impl.db;

import io.scriptor.eswin.util.MutableReference;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Supplier;

public final class Table {

    public interface ColumnGenerator {

        @NotNull Column from(final @NotNull Table table, final @NotNull String name);
    }

    private final Schema schema;
    private final String name;
    private final Set<Column> columns = new HashSet<>();
    private final MutableReference<PrimaryKey> primaryKey = new MutableReference<>();
    private final Set<ForeignKey> importedKeys = new HashSet<>();
    private final Set<ForeignKey> exportedKeys = new HashSet<>();
    private final Set<UniqueConstraint> uniqueConstraints = new HashSet<>();

    public Table(final @NotNull Schema schema, final @NotNull String name) {
        this.schema = schema;
        this.name = name;
    }

    public @NotNull Column column(final @NotNull String name) {
        return columns
                .stream()
                .filter(column -> column.name().equals(name))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("column '%s.%s'".formatted(this, name)));
    }

    public @NotNull Column column(final @NotNull String name, final @NotNull ColumnGenerator generator) {
        final var opt = columns
                .stream()
                .filter(column -> column.name().equals(name))
                .findAny();
        if (opt.isPresent())
            return opt.get();
        final var column = generator.from(this, name);
        columns.add(column);
        return column;
    }

    public @NotNull ForeignKey importedKey(final @NotNull String name) {
        return importedKeys
                .stream()
                .filter(key -> key.name().equals(name))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("imported key '%s.%s'".formatted(this, name)));
    }

    public @NotNull ForeignKey exportedKey(final @NotNull String name) {
        return exportedKeys
                .stream()
                .filter(key -> key.name().equals(name))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("exported key '%s.%s'".formatted(this, name)));
    }

    public @NotNull UniqueConstraint uniqueConstraint(final @NotNull String name) {
        return uniqueConstraints
                .stream()
                .filter(constraint -> constraint.name().equals(name))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("unique constraint '%s.%s'".formatted(this, name)));
    }

    public @NotNull UniqueConstraint uniqueConstraint(
            final @NotNull String name,
            final @NotNull Supplier<UniqueConstraint> supplier
    ) {
        final var opt = uniqueConstraints
                .stream()
                .filter(constraint -> constraint.name().equals(name))
                .findAny();
        if (opt.isPresent())
            return opt.get();
        final var uniqueConstraint = supplier.get();
        uniqueConstraints.add(uniqueConstraint);
        return uniqueConstraint;
    }

    public @NotNull Schema schema() {
        return schema;
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull Set<Column> columns() {
        return columns;
    }

    public @NotNull MutableReference<PrimaryKey> primaryKey() {
        return primaryKey;
    }

    public @NotNull Set<ForeignKey> importedKeys() {
        return importedKeys;
    }

    public @NotNull Set<ForeignKey> exportedKeys() {
        return exportedKeys;
    }

    public @NotNull Set<UniqueConstraint> uniqueConstraints() {
        return uniqueConstraints;
    }

    @Override
    public @NotNull String toString() {
        return "%s.%s".formatted(schema, name);
    }
}
