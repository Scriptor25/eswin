package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.EslValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface EslExpression {

    <T> @NotNull EslValue<T> evaluate(final @NotNull EslFrame frame, final @NotNull Class<T> type);

    default @NotNull EslValue<?> evaluate(final @NotNull EslFrame frame) {
        return evaluate(frame, Object.class);
    }

    default <V> void observe(
            final @NotNull EslFrame frame,
            final @NotNull Consumer<V> observer,
            final @NotNull Class<V> type
    ) {
        throw new UnsupportedOperationException();
    }

    default void observe(final @NotNull EslFrame frame, final @NotNull Consumer<Object> observer) {
        observe(frame, observer, Object.class);
    }
}
