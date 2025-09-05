package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;

public record EslCallExpression(@NotNull EslExpression callee, @NotNull EslExpression[] arguments)
        implements EslExpression {

    @Override
    public Value evaluate(final @NotNull EslFrame frame) {
        final var callee = this.callee.evaluateCallee(frame);
        final var arguments = Arrays.stream(this.arguments)
                                    .map(argument -> argument.evaluate(frame))
                                    .toArray(Value[]::new);

        return callee.call(arguments);
    }
}
