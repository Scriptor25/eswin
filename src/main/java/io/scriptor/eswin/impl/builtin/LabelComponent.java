package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static io.scriptor.eswin.component.Constants.parseSwing;

@Component("label")
public class LabelComponent extends ComponentBase {

    private final JLabel root;

    public LabelComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);

        apply(root = new JLabel());

        if (attributes.has("h-align"))
            root.setHorizontalAlignment(parseSwing(attributes.get("h-align")));
        if (attributes.has("v-align"))
            root.setVerticalAlignment(parseSwing(attributes.get("v-align")));

        observe("#text", root::setText, String.class);
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return root;
    }
}
