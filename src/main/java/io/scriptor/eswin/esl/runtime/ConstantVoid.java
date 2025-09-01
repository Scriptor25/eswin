package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public class ConstantVoid implements Constant {

    @Override
    public @NotNull Object value() {
        throw new IllegalStateException();
    }
}
