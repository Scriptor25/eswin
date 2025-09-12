package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import io.scriptor.eswin.component.context.ContextProvider.ContextFrame;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@Component("menu")
public class MenuComponent extends ComponentBase {

    private final JMenu root;
    private final MenuContext context = new MenuContext() {
        @Override
        public void addMenu(final @NotNull JMenu menu) {
            root.add(menu);
        }

        @Override
        public void addMenuItem(final @NotNull JMenuItem item) {
            root.add(item);
        }
    };

    private ContextFrame frame;

    public MenuComponent(final @NotNull ComponentInfo info) {
        super(info.addObservedAttribute("label"));

        apply(root = new JMenu());
        observe("label", root::setText, String.class);

        if (getProvider().provides(MenuContext.class)) {
            getProvider().use(MenuContext.class).addMenu(root);
        } else {
            getProvider().use(MenuBarContext.class).addMenu(root);
        }
    }

    @Override
    protected void onBeginFrame() {
        super.onBeginFrame();
        frame = getProvider().provide(MenuContext.class, context);
    }

    @Override
    protected void onEndFrame() {
        super.onEndFrame();
        frame.close();
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
