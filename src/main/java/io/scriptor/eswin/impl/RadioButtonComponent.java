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

@Component("radio-button")
public class RadioButtonComponent extends ActionComponentBase {

    private final JRadioButton root;

    public RadioButtonComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);

        apply(root = new JRadioButton());

        if (attributes.has("h-align"))
            root.setHorizontalAlignment(parseSwing(attributes.get("h-align")));

        if (attributes.has("v-align"))
            root.setVerticalAlignment(parseSwing(attributes.get("v-align")));

        observe("#text", root::setText, String.class);
    }

    @Override
    public void addListener(final @NotNull ActionListener listener) {
        root.addActionListener(listener);
    }

    @Override
    public @NotNull JRadioButton getJRoot() {
        return root;
    }
}
