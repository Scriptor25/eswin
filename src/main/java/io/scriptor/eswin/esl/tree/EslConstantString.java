package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.ConstantString;
import io.scriptor.eswin.esl.runtime.Value;
import org.jetbrains.annotations.NotNull;

public record EslConstantString(@NotNull String value) implements EslConstant {

    @Override
    public Value evaluate(final @NotNull EslFrame frame) {
        return new ConstantString(value);
    }
}
