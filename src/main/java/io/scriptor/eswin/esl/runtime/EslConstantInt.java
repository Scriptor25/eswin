package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public record EslConstantInt(long val) implements EslConstant<Long> {

    @Override
    public @NotNull Long value() {
        return val;
    }
}
