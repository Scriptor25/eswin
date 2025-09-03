package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.stream.Stream;

@Component("panel")
public class PanelComponent extends ComponentBase {

    private final JPanel panel;
    private final GridBagLayout layout;

    public PanelComponent(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);

        apply(panel = new JPanel());

        panel.setLayout(layout = new GridBagLayout());

        if (Constants.DEBUG)
            panel.setBackground(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
    }

    @Override
    public @NotNull Stream<JComponent> getJRoot() {
        return Stream.of(panel);
    }

    @Override
    public void putChild(final @NotNull String id, final @NotNull ComponentBase child) {
        super.putChild(id, child);

        final var constraints = child.getConstraints(new GridBagConstraints());

        child.getJRoot().forEach(component -> {
            panel.add(component);
            layout.setConstraints(component, constraints);
        });
    }
}
