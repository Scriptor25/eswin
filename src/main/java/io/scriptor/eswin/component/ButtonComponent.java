package io.scriptor.eswin.component;

import io.scriptor.eswin.esl.EslFrame;
import io.scriptor.eswin.esl.EslGrammar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionListener;

@Component("button")
public class ButtonComponent extends ComponentBase {

    private final JButton button;

    private static @NotNull ActionListener getAction(
            final @NotNull String action,
            final @Nullable ComponentBase container
    ) {
        final var grammar    = new EslGrammar();
        final var expression = grammar.parse(action);

        System.out.println(expression);

        return event -> {
            final var frame = new EslFrame(container, event);
            expression.evaluate(frame);
        };
    }

    public ButtonComponent(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);

        button = new JButton(text);

        if (attributes.has("tooltip"))
            button.setToolTipText(attributes.get("tooltip"));

        if (attributes.has("action")) {
            button.addActionListener(getAction(attributes.get("action"), container));
        }
    }

    @Override
    public @NotNull JButton getJRoot() {
        return button;
    }

    public @NotNull String getText() {
        return button.getText();
    }

    public void setText(final @NotNull String text) {
        button.setText(text);
    }

    public boolean isEnabled() {
        return button.isEnabled();
    }

    public void setEnabled(final boolean enabled) {
        button.setEnabled(enabled);
    }
}
