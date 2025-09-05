package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public record EslConstantString(@NotNull String val) implements EslConstant<String> {

    @Override
    public @NotNull String value() {
        return val;
    }
}
