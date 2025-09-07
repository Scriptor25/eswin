package io.scriptor.eswin.impl.db;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public final class Database extends DatabaseRef {

    private final Set<SchemaRef> schemas = new HashSet<>();

    public Database(final @NotNull DatabaseRef ref) {
        super(ref.server(), ref.name());
    }

    public Database(final @NotNull Server server, final @NotNull String name) {
        super(server, name);
    }

    public @NotNull SchemaRef schema(final @NotNull String name) {
        return schemas.stream()
                      .filter(ref -> ref.name().equals(name))
                      .findAny()
                      .orElseThrow();
    }

    public @NotNull Set<SchemaRef> schemas() {
        return schemas;
    }
}
