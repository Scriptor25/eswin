package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@Component("label")
public class LabelComponent extends ComponentBase {

    private final JLabel root;

    public LabelComponent(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);

        root = new JLabel();

        if (text.startsWith("$")) {
            if (container == null)
                throw new IllegalStateException();

            container.observe(text.substring(1), value -> root.setText(value.toString()));
        }

        if (attributes.has("halign"))
            root.setHorizontalAlignment(Constants.getSwing(attributes.get("halign")));
        if (attributes.has("valign"))
            root.setVerticalAlignment(Constants.getSwing(attributes.get("valign")));
    }

    @Override
    public @NotNull JLabel getJRoot() {
        return root;
    }
}
