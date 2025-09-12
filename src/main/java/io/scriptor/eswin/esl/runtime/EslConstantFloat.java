package io.scriptor.eswin.esl.runtime;

import io.scriptor.eswin.util.Result;
import org.jetbrains.annotations.NotNull;

public record EslConstantFloat(double val) implements EslConstant<Double> {

    @Override
    public @NotNull Result<Double> value() {
        return Result.ok(val);
    }
}
