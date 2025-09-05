package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;

public interface AttributeSet {

    boolean has(final @NotNull String name);

    @NotNull String get(final @NotNull String name);

    default @NotNull String get(final @NotNull String name, final @NotNull String defaultValue) {
        if (has(name))
            return get(name);
        return defaultValue;
    }
}
