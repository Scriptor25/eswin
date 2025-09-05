package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.ActionComponentBase;
import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionListener;

import static io.scriptor.eswin.component.Constants.parseSwing;

@Component("text-field")
public class TextFieldComponent extends ActionComponentBase {

    private final JTextField root;

    public TextFieldComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);

        apply(root = new JTextField());

        if (attributes.has("columns"))
            root.setColumns(Integer.parseUnsignedInt(attributes.get("columns"), 10));

        if (attributes.has("h-align"))
            root.setHorizontalAlignment(parseSwing(attributes.get("h-align")));

        if (attributes.has("default"))
            root.setText(attributes.get("default"));

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

    public @NotNull String getText() {
        return root.getText();
    }
}
