package io.scriptor.eswin.esl.runtime;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.tree.EslExpression;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface Constant extends Value, EslExpression {

    @NotNull Object value();

    @Override
    default @NotNull Value evaluate(final @NotNull EslFrame frame) {
        return this;
    }

    @Override
    default void observe(final @NotNull EslFrame frame, final @NotNull Consumer<Object> observer) {
        observer.accept(value());
    }
}
