package io.scriptor.eswin.esl.runtime;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.tree.EslExpression;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface EslConstant<T> extends EslValue<T>, EslExpression {

    @Override
    default <V> @NotNull EslValue<V> evaluate(final @NotNull EslFrame frame, final @NotNull Class<V> type) {
        return (EslConstant<V>) this;
    }

    @Override
    default <V> void observe(
            final @NotNull EslFrame frame,
            final @NotNull Consumer<V> observer,
            final @NotNull Class<V> type
    ) {
        observer.accept(type.cast(value()));
    }
}
