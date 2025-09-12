package io.scriptor.eswin.esl.runtime;

import io.scriptor.eswin.util.Result;
import org.jetbrains.annotations.NotNull;

public record EslConstantChar(int val) implements EslConstant<Integer> {

    @Override
    public @NotNull Result<Integer> value() {
        return Result.ok(val);
    }
}
