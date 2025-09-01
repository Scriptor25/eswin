package io.scriptor.eswin.esl.tree;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.runtime.ConstantPackage;
import io.scriptor.eswin.esl.runtime.ObjectValue;
import io.scriptor.eswin.esl.runtime.Value;
import org.jetbrains.annotations.NotNull;

public record EslNameExpression(@NotNull String value) implements EslExpression {

    @Override
    public Value evaluate(final @NotNull EslFrame frame) {
        if (value.equals("this"))
            return new ObjectValue(frame.self());
        if (value.equals("event"))
            return new ObjectValue(frame.event());
        return new ConstantPackage(value);
    }
}
