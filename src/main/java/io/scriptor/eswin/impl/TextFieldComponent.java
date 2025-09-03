package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.stream.Stream;

import static io.scriptor.eswin.component.Constants.getSwing;
import static io.scriptor.eswin.util.DynamicUtil.getActionListener;

@Component("text-field")
public class TextFieldComponent extends ComponentBase {

    private final JTextField field;

    public TextFieldComponent(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);

        apply(field = new JTextField(text));

        if (attributes.has("columns"))
            field.setColumns(Integer.parseUnsignedInt(attributes.get("columns"), 10));

        if (attributes.has("h-align"))
            field.setHorizontalAlignment(getSwing(attributes.get("h-align")));

        if (attributes.has("action"))
            field.addActionListener(getActionListener(container, attributes.get("action")));
    }

    @Override
    public @NotNull Stream<JComponent> getJRoot() {
        return Stream.of(field);
    }
}
