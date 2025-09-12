package io.scriptor.eswin.impl.model;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;

public final class Database extends DatabaseRef {

    private final Set<SchemaRef> schemas = new HashSet<>();

    public Database(final @NotNull DatabaseRef ref) {
        super(ref.getServer(), ref.getName());
    }

    public Database(final @NotNull Server server, final @NotNull String name) {
        super(server, name);
    }

    public @NotNull SchemaRef getSchema(final @NotNull String name) {
        return schemas.stream()
                      .filter(ref -> ref.getName().equals(name))
                      .findAny()
                      .orElseThrow(() -> new NoSuchElementException("schema '%s:%s'".formatted(this, name)));
    }

    public @NotNull Stream<SchemaRef> getSchemas() {
        return schemas.stream();
    }

    public void addSchema(final @NotNull SchemaRef schema) {
        schemas.add(schema);
    }

    public void removeSchema(final @NotNull SchemaRef schema) {
        schemas.remove(schema);
    }
}
