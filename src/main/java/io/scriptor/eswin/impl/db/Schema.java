package io.scriptor.eswin.impl.db;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public final class Schema extends SchemaRef {

    public interface TableGenerator {

        @NotNull Table from(final @NotNull Schema schema, final @NotNull String name);
    }

    private final Set<Table> tables = new HashSet<>();

    public Schema(final @NotNull SchemaRef ref) {
        super(ref.database(), ref.name());
    }

    public Schema(final @NotNull Database database, final @NotNull String name) {
        super(database, name);
    }

    public @NotNull Table table(final @NotNull String name) {
        return tables.stream()
                     .filter(table -> table.name().equals(name))
                     .findAny()
                     .orElseThrow(() -> new NoSuchElementException("table '%s.%s'".formatted(this, name)));
    }

    public @NotNull Table table(final @NotNull String name, final @NotNull Schema.TableGenerator generator) {
        final var opt = tables.stream()
                              .filter(table -> table.name().equals(name))
                              .findAny();
        if (opt.isPresent())
            return opt.get();
        final var table = generator.from(this, name);
        tables.add(table);
        return table;
    }

    public @NotNull Set<Table> tables() {
        return tables;
    }
}
