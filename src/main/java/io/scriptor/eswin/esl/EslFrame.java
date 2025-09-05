package io.scriptor.eswin.esl;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public final class EslFrame {

    private final Map<String, Object> values = new HashMap<>();

    public boolean has(final @NotNull String name) {
        return values.containsKey(name);
    }

    public <T> @NotNull T get(final @NotNull String name, final @NotNull Class<T> type) {
        if (!values.containsKey(name))
            throw new NoSuchElementException(name);
        return type.cast(values.get(name));
    }

    public void put(final @NotNull String name, final @NotNull Object value) {
        values.put(name, value);
    }
}
