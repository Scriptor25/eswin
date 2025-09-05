package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public record EslConstantFloat(double val) implements EslConstant<Double> {

    @Override
    public @NotNull Double value() {
        return val;
    }
}
