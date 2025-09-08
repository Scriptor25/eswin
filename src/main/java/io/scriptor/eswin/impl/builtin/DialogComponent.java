package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ContextProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

@Component("dialog")
public class DialogComponent extends ComponentBase {

    private final JPanel root;

    public DialogComponent(
            final @NotNull ContextProvider provider,
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(provider, parent, attributes, text);

        apply(root = new JPanel());

        root.setLayout(new GridBagLayout());

        root.setOpaque(true);
        root.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED));
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return root;
    }

    @Override
    public void attach(final @NotNull Container container, final boolean constraint) {
        if (container instanceof JLayeredPane layered) {
            layered.add(root, JLayeredPane.POPUP_LAYER);

            onAttached();

            getChildren().forEach(child -> child.attach(root, true));
            return;
        }

        attach(container.getParent(), constraint);
    }
}
