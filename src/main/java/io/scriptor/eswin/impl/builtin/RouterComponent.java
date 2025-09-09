package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import io.scriptor.eswin.component.context.ContextProvider;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component("router")
public class RouterComponent extends ComponentBase {

    private final List<RouteComponent> routes = new ArrayList<>();
    private RouteComponent active;

    private final RouterContext context = new RouterContext() {

        @Override
        public void addRoute(final @NotNull RouteComponent route) {
            routes.add(route);
        }

        @Override
        public void setActive(final @NotNull RouteComponent route) {
            final var container = detach();

            active = route;

            attach(container, container.getLayout() instanceof GridBagLayout);

            container.revalidate();
            container.repaint();
        }

        @Override
        public void setActive(final @NotNull String id) {
            routes.stream()
                  .filter(route -> route.getId().equals(id))
                  .findAny()
                  .ifPresent(this::setActive);
        }
    };
    private ContextProvider.ContextFrame frame;

    public RouterComponent(final @NotNull ComponentInfo info) {
        super(info);
    }

    @Override
    protected void onBeginFrame() {
        frame = getProvider().provide(RouterContext.class, context);
    }

    @Override
    protected void onEndFrame() {
        if (!routes.isEmpty())
            active = routes.getFirst();
        frame.close();
    }

    @Override
    public void attach(final @NotNull Container container, final boolean constraint) {
        if (active == null)
            throw new IllegalStateException();

        active.attach(container, constraint);

        onAttached();
    }

    @Override
    public boolean attached() {
        if (active == null)
            throw new IllegalStateException();
        return active.attached();
    }

    @Override
    public @NotNull Container detach() {
        if (active == null)
            throw new IllegalStateException();
        return active.detach();
    }
}
