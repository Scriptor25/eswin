package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.Value;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface EslExpression {

    Value evaluate(final @NotNull EslFrame frame);

    default Value evaluateCallee(final @NotNull EslFrame frame) {
        return evaluate(frame);
    }

    default void observe(final @NotNull EslFrame frame, final @NotNull Consumer<Object> observer) {
        throw new UnsupportedOperationException();
    }
}
