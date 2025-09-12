package io.scriptor.eswin.component.attribute;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public interface AttributeSet {

    boolean has(final @NotNull String name);

    @NotNull String get(final @NotNull String name);

    @NotNull String get(final @NotNull String name, final @NotNull String defaultValue);

    @NotNull Stream<String> unused();
}
