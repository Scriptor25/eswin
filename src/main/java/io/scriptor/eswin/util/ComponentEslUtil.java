package io.scriptor.eswin.util;

import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.action.ActionComponentBase;
import io.scriptor.eswin.component.action.ActionListener;
import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.EslGrammar;
import io.scriptor.eswin.esl.runtime.EslConstantString;
import io.scriptor.eswin.esl.runtime.EslErrorValue;
import io.scriptor.eswin.esl.tree.EslExpression;
import io.scriptor.eswin.grammar.GrammarContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ComponentEslUtil {

    private ComponentEslUtil() {
    }

    public static <C extends ActionComponentBase<C, P>, P> @NotNull ActionListener<C, P> getActionListener(
            final @NotNull ComponentBase parent,
            final @NotNull String action
    ) {
        final var grammar    = new EslGrammar();
        final var expression = grammar.parse("<unknown>", action);

        return event -> {
            final var frame = new EslFrame();
            frame.put("this", parent);
            frame.put("event", event);

            final var value = expression.evaluate(frame, void.class);

            if (value instanceof EslErrorValue<?>(final @NotNull Throwable thrown)) {
                Log.warn("while processing expression: %s", thrown);
                parent.notify("#thrown", thrown);
            }
        };
    }

    public static void observeText(
            final @NotNull ComponentBase parent,
            final @NotNull String text,
            final @NotNull Consumer<String> consumer
    ) {
        final var grammar = new EslGrammar();
        final var context = new GrammarContext("<unknown>", text);

        final var builder = new StringBuilder();

        final List<EslExpression> expressions = new ArrayList<>();

        while (context.get() >= 0) {
            if (context.skipIf('{')) {
                if (context.skipIf('{')) {
                    builder.appendCodePoint('{');
                    continue;
                }

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

        final List<Map.Entry<EslExpression, String>> segments = new ArrayList<>();
        expressions.forEach(expression -> segments.add(new Map.Entry<>() {

            String value = "";

            @Override
            public @NotNull EslExpression getKey() {
                return expression;
            }

            @Override
            public @NotNull String getValue() {
                return value;
            }

            @Override
            public @NotNull String setValue(final @NotNull String value) {
                final var previous = this.value;
                this.value = value;
                return previous;
            }
        }));

        final var frame = new EslFrame();
        frame.put("this", parent);

        segments.forEach(segment -> segment
                .getKey()
                .observe(frame, value -> {
                    segment.setValue(value.toString());
                    consumer.accept(String.join("", segments
                            .stream()
                            .map(Map.Entry::getValue)
                            .toArray(String[]::new)));
                }, thrown -> {
                    Log.warn("while processing expression segments: %s", thrown);
                    parent.notify("#thrown", thrown);
                }));
    }
}
