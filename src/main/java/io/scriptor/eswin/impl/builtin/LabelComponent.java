package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static io.scriptor.eswin.component.attribute.AttributeUtil.parseSwing;

@Component("label")
public class LabelComponent extends ComponentBase {

    private final JLabel root;

    public LabelComponent(final @NotNull ComponentInfo info) {
        super(info.setObserveText(true));

        apply(root = new JLabel());

        if (getAttributes().has("h-align"))
            root.setHorizontalAlignment(parseSwing(getAttributes().get("h-align")));
        if (getAttributes().has("v-align"))
            root.setVerticalAlignment(parseSwing(getAttributes().get("v-align")));

        observe("#text", root::setText, String.class);
    }

    @Override
    public boolean hasJRoot() {
        return true;
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return root;
    }
}
