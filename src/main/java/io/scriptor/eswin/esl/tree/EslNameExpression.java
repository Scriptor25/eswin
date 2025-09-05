package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.EslObjectValue;
import io.scriptor.eswin.esl.runtime.EslPackageValue;
import io.scriptor.eswin.esl.runtime.EslValue;
import org.jetbrains.annotations.NotNull;

public record EslNameExpression(@NotNull String value) implements EslExpression {

    @Override
    public <T> @NotNull EslValue<T> evaluate(final @NotNull EslFrame frame, final @NotNull Class<T> type) {
        if (frame.has(value))
            return new EslObjectValue<>(frame.get(value, type));
        return new EslPackageValue<>(value);
    }
}
