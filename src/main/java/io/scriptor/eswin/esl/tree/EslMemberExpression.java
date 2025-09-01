package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.Value;
import org.jetbrains.annotations.NotNull;

public record EslMemberExpression(@NotNull EslExpression object, @NotNull String member) implements EslExpression {

    @Override
    public Value evaluate(final @NotNull EslFrame frame) {
        final var object = this.object.evaluate(frame);

        return object.field(member);
    }
}
