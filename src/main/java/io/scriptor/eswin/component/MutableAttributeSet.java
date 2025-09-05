package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MutableAttributeSet implements AttributeSet {

    private final Map<String, String> attributes = new HashMap<>();

    public void put(final @NotNull String key, final @NotNull String value) {
        attributes.put(key, value);
    }

    @Override
    public boolean has(final @NotNull String key) {
        return attributes.containsKey(key);
    }

    @Override
    public @NotNull String get(final @NotNull String key) {
        if (!attributes.containsKey(key))
            throw new IllegalStateException();
        return attributes.get(key);
    }
}
