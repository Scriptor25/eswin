package io.scriptor.eswin.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public final class RefMap<T, R> {

    private final Map<R, T> map = new HashMap<>();

    public @NotNull T get(final @NotNull R ref, final @NotNull Function<R, T> mapping) {
        return map.computeIfAbsent(ref, mapping);
    }

    public void clear() {
        map.clear();
    }

    public @NotNull Stream<T> stream() {
        return map.values().stream();
    }
}
