package io.scriptor.eswin.impl.model;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;

public final class Server {

    private final ServerRef ref;
    private final Set<DatabaseRef> databases = new HashSet<>();

    public Server(final @NotNull ServerRef ref) {
        this.ref = ref;
    }

    public @NotNull String getLabel() {
        return ref.label();
    }

    public @NotNull String getURL() {
        return ref.url();
    }

    public @NotNull String getUsername() {
        return ref.username();
    }

    public @NotNull String getPassword() {
        return ref.password();
    }

    public @NotNull DatabaseRef getDatabase(final @NotNull String name) {
        return databases.stream()
                        .filter(ref -> ref.getName().equals(name))
                        .findAny()
                        .orElseThrow(() -> new NoSuchElementException("database '%s'".formatted(name)));
    }

    public @NotNull Stream<DatabaseRef> getDatabases() {
        return databases.stream();
    }

    public void addDatabase(final @NotNull DatabaseRef database) {
        databases.add(database);
    }

    public void removeDatabase(final @NotNull DatabaseRef database) {
        databases.remove(database);
    }
}
