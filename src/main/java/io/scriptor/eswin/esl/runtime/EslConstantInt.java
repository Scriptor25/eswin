package io.scriptor.eswin.esl.runtime;

import io.scriptor.eswin.util.Result;
import org.jetbrains.annotations.NotNull;

public record EslConstantInt(long val) implements EslConstant<Long> {

    @Override
    public @NotNull Result<Long> value() {
        return Result.ok(val);
    }
}
