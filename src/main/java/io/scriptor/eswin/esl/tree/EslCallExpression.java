package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.EslValue;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public record EslCallExpression(@NotNull EslExpression callee, @NotNull EslExpression[] arguments)
        implements EslExpression {

    @Override
    public <T> @NotNull EslValue<T> evaluate(final @NotNull EslFrame frame, final @NotNull Class<T> type) {
        final var callee = this.callee.evaluate(frame);
        final var arguments = Arrays.stream(this.arguments)
                                    .map(argument -> argument.evaluate(frame))
                                    .toArray(EslValue[]::new);

        return callee.call(arguments, type);
    }
}
