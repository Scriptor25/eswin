package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ContextProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

@Component("panel")
public class PanelComponent extends ComponentBase {

    private final JPanel panel;

    public PanelComponent(
            final @NotNull ContextProvider provider,
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(provider, parent, attributes, text);

        apply(panel = new JPanel());

        panel.setLayout(new GridBagLayout());
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return panel;
    }

    @Override
    public void insert(final @NotNull String id, final @NotNull ComponentBase child) {
        super.insert(id, child);

        child.attach(panel, true);
    }
}
