package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import io.scriptor.eswin.component.context.ContextProvider.ContextFrame;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

@Component("menu-bar")
public class MenuBarComponent extends ComponentBase {

    private final JMenuBar root;
    private final MenuBarContext context = new MenuBarContext() {

        @Override
        public void addMenu(final @NotNull JMenu menu) {
            root.add(menu);
        }
    };

    private ContextFrame frame;
    private Container container;

    public MenuBarComponent(final @NotNull ComponentInfo info) {
        super(info);

        apply(root = new JMenuBar());
    }

    @Override
    protected void onBeginFrame() {
        super.onBeginFrame();
        frame = getProvider().provide(MenuBarContext.class, context);
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

    private void attach(final @NotNull Container container) {
        if (container instanceof JFrame window) {
            window.setJMenuBar(root);

            getChildren().forEach(ComponentBase::notifyAttached);

            onAttached();
            return;
        }

        if (container.getParent() == null)
            throw new IllegalStateException();

        attach(container.getParent());
    }

    @Override
    public void attach(final @NotNull Container container, final boolean constraint) {
        this.container = container;
        attach(container);
    }

    @Override
    public void attach(
            final @NotNull Container container,
            final boolean constraint,
            final @NotNull GridBagConstraints constraints
    ) {
        this.container = container;
        attach(container);
    }

    @Override
    public @NotNull Container detach() {
        getChildren().forEach(ComponentBase::notifyDetached);

        final var container = this.container;
        this.container = null;

        onDetached();
        return container;
    }
}
