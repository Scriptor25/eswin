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

@Component("button")
public class ButtonComponent extends ActionComponentBase {

    private final JButton root;

    public ButtonComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);

        apply(root = new JButton());
        root.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        observe("#text", root::setText, String.class);
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
