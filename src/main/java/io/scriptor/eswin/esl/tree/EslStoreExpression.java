package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.EslErrorValue;
import io.scriptor.eswin.esl.runtime.EslValue;
import org.jetbrains.annotations.NotNull;

public record EslStoreExpression(@NotNull EslExpression dst, @NotNull EslExpression src) implements EslExpression {

    @Override
    public <T> @NotNull EslValue<T> evaluate(final @NotNull EslFrame frame, final @NotNull Class<T> type) {
        final var dst = this.dst.evaluate(frame, type);
        final var src = this.src.evaluate(frame, type);

        final var result = src.value();
        if (result.error())
            return new EslErrorValue<>(result.thrown());

        return dst.store(result.value());
    }
}
