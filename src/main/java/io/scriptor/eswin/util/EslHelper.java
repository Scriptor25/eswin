package io.scriptor.eswin.util;

import io.scriptor.eswin.component.ActionComponentBase;
import io.scriptor.eswin.component.ActionListener;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.EslGrammar;
import io.scriptor.eswin.esl.runtime.EslConstantString;
import io.scriptor.eswin.esl.tree.EslExpression;
import io.scriptor.eswin.grammar.GrammarContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EslHelper {

    private EslHelper() {
    }

    public static <C extends ActionComponentBase<C, P>, P> @NotNull ActionListener<C, P> getActionListener(
            final @Nullable ComponentBase parent,
            final @NotNull String action
    ) {
        final var grammar    = new EslGrammar();
        final var expression = grammar.parse("<unknown>", action);

        return event -> {
            final var frame = new EslFrame();
            if (parent != null)
                frame.put("this", parent);
            frame.put("event", event);
            expression.evaluate(frame, void.class);
        };
    }

    public static @NotNull EslExpression[] getSegments(final @NotNull String text) {
        final var grammar = new EslGrammar();
        final var context = new GrammarContext("<unknown>", text);

        final var builder = new StringBuilder();

        final List<EslExpression> expressions = new ArrayList<>();

        while (context.get() >= 0) {
            if (context.skipIf('{')) {
                expressions.add(new EslConstantString(builder.toString()));
                builder.delete(0, builder.length());

                final var expression = grammar.parse(context);
                expressions.add(expression);

                context.whitespace();
                if (!context.skipIf('}'))
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
            final @Nullable ComponentBase parent,
            final @NotNull EslExpression[] expressions,
            final @NotNull SegmentObserver observer
    ) {
        final var frame = new EslFrame();
        if (parent != null)
            frame.put("this", parent);
        for (int i = 0; i < expressions.length; ++i) {
            final var index = i;
            expressions[i].observe(frame, value -> observer.notify(index, value.toString()));
        }
    }
}
