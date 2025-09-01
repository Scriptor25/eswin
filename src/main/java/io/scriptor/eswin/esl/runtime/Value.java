package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public interface Value {

    @NotNull Object value();

    default @NotNull Value store(final @NotNull Object value) {
        throw new IllegalStateException();
    }

    default @NotNull Value field(final @NotNull String name) {
        throw new IllegalStateException();
    }

    default @NotNull Value call(final @NotNull Value[] arguments) {
        throw new IllegalStateException();
    }
}
