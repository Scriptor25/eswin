package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.ActionComponentBase;
import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static io.scriptor.eswin.util.EslUtil.getSegments;
import static io.scriptor.eswin.util.EslUtil.observeSegments;

@Component("button")
public class ButtonComponent extends ActionComponentBase {

    private final JButton root;
    private final String[] segments;

    public ButtonComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);

        final var expressions = getSegments(text);
        segments = new String[expressions.length];

        apply(root = new JButton());

        observeSegments(parent, expressions, (index, value) -> {
            segments[index] = value.toString();
            root.setText(String.join("", segments));
        });

        root.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void addListener(final @NotNull ActionListener listener) {
        root.addActionListener(listener);
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return root;
    }

    public boolean isEnabled() {
        return root.isEnabled();
    }

    public void setEnabled(final boolean enabled) {
        root.setEnabled(enabled);
    }
}
