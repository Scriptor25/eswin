package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.ConstantInt;
import io.scriptor.eswin.esl.runtime.Value;
import org.jetbrains.annotations.NotNull;

public record EslConstantInt(long value) implements EslConstant {

    @Override
    public Value evaluate(final @NotNull EslFrame frame) {
        return new ConstantInt(value);
    }
}
