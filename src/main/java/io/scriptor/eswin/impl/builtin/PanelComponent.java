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
    public @NotNull JComponent getJRoot() {
        return root;
    }

    @Override
    public void attach(
            final @NotNull Container container,
            final boolean constraint,
            final @NotNull GridBagConstraints constraints
    ) {
        if (constraint) {
            container.add(root, constraints);
        } else {
            container.add(root);
        }

        getChildren().forEach(child -> child.attach(root, true));

        onAttached();
    }
}
