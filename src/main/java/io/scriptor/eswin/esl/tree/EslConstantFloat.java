package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.ConstantFloat;
import io.scriptor.eswin.esl.runtime.Value;
import org.jetbrains.annotations.NotNull;

public record EslConstantFloat(double value) implements EslConstant {

    @Override
    public Value evaluate(final @NotNull EslFrame frame) {
        return new ConstantFloat(value);
    }
}
