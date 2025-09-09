package io.scriptor.eswin.component.attribute;

import org.jetbrains.annotations.NotNull;

public interface AttributeSet {

    boolean has(final @NotNull String name);

    @NotNull String get(final @NotNull String name);

    @NotNull String get(final @NotNull String name, final @NotNull String defaultValue);
}
