package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AttributeSet {

    private final Map<String, String> attributes = new HashMap<>();

    public AttributeSet() {
    }

    public void put(final @NotNull String key, final @NotNull String value) {
        attributes.put(key, value);
    }

    public boolean has(final @NotNull String key) {
        return attributes.containsKey(key);
    }

    public @NotNull String get(final @NotNull String key) {
        if (!attributes.containsKey(key))
            throw new IllegalStateException();
        return attributes.get(key);
    }

    public int getInt(final @NotNull String key) {
        return Integer.parseInt(get(key));
    }

    @Override
    public String toString() {
        return attributes.toString();
    }
}
