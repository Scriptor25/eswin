package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@Component("route")
public class RouteComponent extends ComponentBase {

    public RouteComponent(final @NotNull ComponentInfo info) {
        super(info);

        final var context = getProvider().use(RouterContext.class);
        context.addRoute(this);
    }
}
