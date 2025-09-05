package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static io.scriptor.eswin.component.Constants.getSwing;
import static io.scriptor.eswin.util.EslUtil.getSegments;
import static io.scriptor.eswin.util.EslUtil.observeSegments;

@Component("label")
public class LabelComponent extends ComponentBase {

    private final JLabel label;
    private final String[] segments;

    public LabelComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);

        final var expressions = getSegments(text);

        apply(label = new JLabel());
        segments = new String[expressions.length];

        observeSegments(parent, expressions, (index, value) -> {
            segments[index] = value.toString();
            label.setText(String.join("", segments));
        });

        if (attributes.has("h-align"))
            label.setHorizontalAlignment(getSwing(attributes.get("h-align")));
        if (attributes.has("v-align"))
            label.setVerticalAlignment(getSwing(attributes.get("v-align")));
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return label;
    }
}
