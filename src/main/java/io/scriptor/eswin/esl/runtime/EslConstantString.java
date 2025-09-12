package io.scriptor.eswin.esl.runtime;

import io.scriptor.eswin.util.Result;
import org.jetbrains.annotations.NotNull;

public record EslConstantString(@NotNull String val) implements EslConstant<String> {

    @Override
    public @NotNull Result<String> value() {
        return Result.ok(val);
    }
}
