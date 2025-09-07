package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Context;
import org.jetbrains.annotations.NotNull;

public interface RouterContext extends Context {

    void addRoute(final @NotNull RouteComponent route);

    void setActive(final @NotNull RouteComponent route);

    void setActive(final @NotNull String id);
}
