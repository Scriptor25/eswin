package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

@Component("panel")
public class PanelComponent extends ComponentBase {

    private final JPanel root;

    public PanelComponent(final @NotNull ComponentInfo info) {
        super(info);

        apply(root = new JPanel());

        root.setLayout(new GridBagLayout());
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
