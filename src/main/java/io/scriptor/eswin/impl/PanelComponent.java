package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

@Component("panel")
public class PanelComponent extends ComponentBase {

    private final JPanel panel;

    public PanelComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);

        apply(panel = new JPanel());

        panel.setLayout(new GridBagLayout());

        if (Constants.DEBUG)
            panel.setBackground(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return panel;
    }

    @Override
    public void addChild(final @NotNull String id, final @NotNull ComponentBase child) {
        super.addChild(id, child);

        child.render(panel, true);
    }
}
