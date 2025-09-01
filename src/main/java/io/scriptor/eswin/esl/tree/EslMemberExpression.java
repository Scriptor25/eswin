package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.Value;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record EslMemberExpression(@NotNull EslExpression object, @NotNull String member) implements EslExpression {

    @Override
    public Value evaluate(final @NotNull EslFrame frame) {
        final var object = this.object.evaluate(frame);

        return object.field(member);
    }

    @Override
    public void observe(final @NotNull EslFrame frame, final @NotNull Consumer<Object> observer) {
        final var object = this.object.evaluate(frame);
        final var value  = object.value();

        if (value instanceof ComponentBase component) {
            component.observe(member, observer);
            return;
        }

        throw new UnsupportedOperationException();
    }
}
