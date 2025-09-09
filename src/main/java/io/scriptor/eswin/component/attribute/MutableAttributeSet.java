package io.scriptor.eswin.component.attribute;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MutableAttributeSet implements AttributeSet {

    private final Map<String, String> attributes = new HashMap<>();

    public void put(final @NotNull String name, final @NotNull String value) {
        attributes.put(name, value);
    }

    @Override
    public boolean has(final @NotNull String name) {
        return attributes.containsKey(name);
    }

    @Override
    public @NotNull String get(final @NotNull String name) {
        if (attributes.containsKey(name))
            return attributes.get(name);
        throw new IllegalStateException();
    }

    @Override
    public @NotNull String get(final @NotNull String name, final @NotNull String defaultValue) {
        if (attributes.containsKey(name))
            return attributes.get(name);
        return defaultValue;
    }
}
