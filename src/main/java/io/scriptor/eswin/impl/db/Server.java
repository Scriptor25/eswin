package io.scriptor.eswin.impl.db;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public final class Server {

    private final ServerRef ref;
    private final Set<DatabaseRef> databases = new HashSet<>();

    public Server(final @NotNull ServerRef ref) {
        this.ref = ref;
    }

    public @NotNull String label() {
        return ref.label();
    }

    public @NotNull String url() {
        return ref.url();
    }

    public @NotNull String username() {
        return ref.username();
    }

    public @NotNull String password() {
        return ref.password();
    }

    public @NotNull DatabaseRef database(final @NotNull String name) {
        return databases.stream()
                        .filter(ref -> ref.name().equals(name))
                        .findAny()
                        .orElseThrow();
    }

    public @NotNull Set<DatabaseRef> databases() {
        return databases;
    }
}
