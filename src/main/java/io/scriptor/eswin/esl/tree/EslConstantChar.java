package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.ConstantChar;
import io.scriptor.eswin.esl.runtime.Value;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record EslConstantChar(int value) implements EslConstant {

    @Override
    public Value evaluate(final @NotNull EslFrame frame) {
        return new ConstantChar(value);
    }

    @Override
    public void observe(final @NotNull EslFrame frame, final @NotNull Consumer<Object> observer) {
        observer.accept(value);
    }
}
