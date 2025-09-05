package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public interface Value {

    default @NotNull Object value() {
        throw new UnsupportedOperationException();
    }

    default @NotNull Value store(final @NotNull Object value) {
        throw new UnsupportedOperationException();
    }

    default @NotNull Value field(final @NotNull String name) {
        throw new UnsupportedOperationException();
    }

    default @NotNull Value call(final @NotNull Value[] arguments) {
        throw new UnsupportedOperationException();
    }
}
