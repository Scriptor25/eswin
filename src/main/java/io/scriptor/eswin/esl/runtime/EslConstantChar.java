package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public record EslConstantChar(int val) implements EslConstant<Integer> {

    @Override
    public @NotNull Integer value() {
        return val;
    }
}
