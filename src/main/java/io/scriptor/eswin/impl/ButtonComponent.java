package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

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
        segments = new String[expressions.length];

        apply(button = new JButton());

        observeSegments(container, expressions, (index, value) -> {
            segments[index] = value.toString();
            button.setText(String.join("", segments));
        });

        if (attributes.has("action"))
            button.addActionListener(getActionListener(container, attributes.get("action")));

        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public @NotNull Stream<JComponent> getJRoot() {
        return Stream.of(button);
    }

    public boolean isEnabled() {
        return button.isEnabled();
    }

    public void setEnabled(final boolean enabled) {
        button.setEnabled(enabled);
    }
}
