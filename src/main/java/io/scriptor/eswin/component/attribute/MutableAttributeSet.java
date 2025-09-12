package io.scriptor.eswin.component.attribute;

import io.scriptor.eswin.util.Usable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MutableAttributeSet implements AttributeSet {

    private final Map<String, Usable<String>> attributes = new HashMap<>();

    public void put(final @NotNull String name, final @NotNull String value) {
        attributes.put(name, new Usable<>(value));
    }

    @Override
    public boolean has(final @NotNull String name) {
        return attributes.containsKey(name);
    }

    @Override
    public @NotNull String get(final @NotNull String name) {
        if (attributes.containsKey(name))
            return attributes.get(name).get();
        throw new IllegalStateException();
    }

    @Override
    public @NotNull String get(final @NotNull String name, final @NotNull String defaultValue) {
        if (attributes.containsKey(name))
            return attributes.get(name).get();
        return defaultValue;
    }

    @Override
    public @NotNull Stream<String> unused() {
        return attributes.entrySet()
                         .stream()
                         .filter(e -> e.getValue().used())
                         .map(Map.Entry::getKey);
    }
}
