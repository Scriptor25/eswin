package io.scriptor.eswin.util;

import io.scriptor.eswin.component.action.ActionComponentBase;
import io.scriptor.eswin.component.action.ActionListener;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.EslGrammar;
import io.scriptor.eswin.esl.runtime.EslConstantString;
import io.scriptor.eswin.esl.tree.EslExpression;
import io.scriptor.eswin.grammar.GrammarContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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

    public static void observeText(
            final @Nullable ComponentBase parent,
            final @NotNull String text,
            final @NotNull Consumer<String> consumer
    ) {
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

        final var expressionSegments = expressions.toArray(EslExpression[]::new);
        final var textSegments       = new String[expressionSegments.length];
        Arrays.fill(textSegments, "");

        final var frame = new EslFrame();
        if (parent != null)
            frame.put("this", parent);

        for (int i = 0; i < expressionSegments.length; ++i) {
            final var index = i;
            expressionSegments[i].observe(frame, value -> {
                textSegments[index] = value.toString();
                consumer.accept(String.join("", textSegments));
            });
        }
    }
}
