package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public class VoidValue implements Value {

    @Override
    public @NotNull Object value() {
        throw new IllegalStateException();
    }
}
