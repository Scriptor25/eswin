package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@Component("menu-item")
public class MenuItemComponent extends ComponentBase {

    private final JMenuItem root;

    public MenuItemComponent(final @NotNull ComponentInfo info) {
        super(info.setObserveText(true));

        apply(root = new JMenuItem());
        observe("#text", root::setText, String.class);

        getProvider().use(MenuContext.class).addMenuItem(root);
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
