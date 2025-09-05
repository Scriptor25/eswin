package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.EslValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record EslMemberExpression(@NotNull EslExpression object, @NotNull String member) implements EslExpression {

    @Override
    public <T> @NotNull EslValue<T> evaluate(final @NotNull EslFrame frame, final @NotNull Class<T> type) {
        final var object = this.object.evaluate(frame);
        return object.field(member, type);
    }

    @Override
    public <T> void observe(
            final @NotNull EslFrame frame,
            final @NotNull Consumer<T> observer,
            final @NotNull Class<T> type
    ) {
        final var object    = this.object.evaluate(frame, ComponentBase.class);
        final var component = object.value();

        component.observe(member, observer, type);
    }
}
