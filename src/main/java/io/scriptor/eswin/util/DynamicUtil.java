package io.scriptor.eswin.util;

import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.EslGrammar;
import io.scriptor.eswin.esl.tree.EslConstantString;
import io.scriptor.eswin.esl.tree.EslExpression;
import io.scriptor.eswin.grammar.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class DynamicUtil {

    private DynamicUtil() {
    }

    public static @NotNull ActionListener getActionListener(
            final @Nullable ComponentBase container,
            final @NotNull String action
    ) {
        final var grammar    = new EslGrammar();
        final var expression = grammar.parse("<unknown>", action);

        return event -> {
            final var frame = new EslFrame(container, event);
            expression.evaluate(frame);
        };
    }

    public static @NotNull EslExpression[] getSegments(final @NotNull String text) {
        final var grammar = new EslGrammar();
        final var context = new Context("<unknown>", text);

        final var builder = new StringBuilder();

        final List<EslExpression> expressions = new ArrayList<>();

        while (context.get() >= 0) {
            if (context.skipif('{')) {
                expressions.add(new EslConstantString(builder.toString()));
                builder.delete(0, builder.length());

                final var expression = grammar.parse(context);
                expressions.add(expression);

                context.whitespace();
                if (!context.skipif('}'))
                    throw new IllegalStateException();
            } else {
                builder.appendCodePoint(context.skip());
            }
        }

        if (!builder.isEmpty()) {
            expressions.add(new EslConstantString(builder.toString()));
        }

        return expressions.toArray(EslExpression[]::new);
    }

    public static void observeSegments(
            final @Nullable ComponentBase container,
            final @NotNull EslExpression[] expressions,
            final @NotNull SegmentObserver observer
    ) {
        final var frame = new EslFrame(container, null);
        for (int i = 0; i < expressions.length; ++i) {
            final var index = i;
            expressions[i].observe(frame, value -> observer.notify(index, value.toString()));
        }
    }
}
