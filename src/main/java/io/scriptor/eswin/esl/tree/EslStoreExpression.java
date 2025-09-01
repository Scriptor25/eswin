package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.Value;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record EslStoreExpression(@NotNull EslExpression dst, @NotNull EslExpression src) implements EslExpression {

    @Override
    public Value evaluate(final @NotNull EslFrame frame) {
        final var dst = this.dst.evaluate(frame);
        final var src = this.src.evaluate(frame);

        return dst.store(src.value());
    }
}
