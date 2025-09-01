package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static io.scriptor.eswin.util.DynamicUtil.*;

@Component("button")
public class ButtonComponent extends ComponentBase {

    private final JButton button;
    private final String[] segments;


    public ButtonComponent(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);

        final var expressions = getSegments(text);

        button = new JButton();
        segments = new String[expressions.length];

        observeSegments(container, expressions, (index, value) -> {
            segments[index] = value.toString();
            button.setText(String.join("", segments));
        });

        if (attributes.has("tooltip")) {
            button.setToolTipText(attributes.get("tooltip"));
        }

        if (attributes.has("action")) {
            button.addActionListener(getActionListener(container, attributes.get("action")));
        }
    }

    @Override
    public @NotNull JButton getJRoot() {
        return button;
    }

    public boolean isEnabled() {
        return button.isEnabled();
    }

    public void setEnabled(final boolean enabled) {
        button.setEnabled(enabled);
    }
}
