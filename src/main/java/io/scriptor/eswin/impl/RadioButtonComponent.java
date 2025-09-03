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

@Component("radio-button")
public class RadioButtonComponent extends ComponentBase {

    private final JRadioButton radio;

    public RadioButtonComponent(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);

        apply(radio = new JRadioButton(text));

        if (attributes.has("h-align"))
            radio.setHorizontalAlignment(getSwing(attributes.get("h-align")));
        if (attributes.has("v-align"))
            radio.setVerticalAlignment(getSwing(attributes.get("v-align")));

        if (attributes.has("action"))
            radio.addActionListener(getActionListener(container, attributes.get("action")));
    }

    @Override
    public @NotNull Stream<JComponent> getJRoot() {
        return Stream.of(radio);
    }
}
