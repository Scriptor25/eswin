package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ContextProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@Component("route")
public class RouteComponent extends ComponentBase {

    public RouteComponent(
            final @NotNull ContextProvider provider,
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(provider, parent, attributes, text);

        final var context = provider.use(RouterContext.class);
        context.addRoute(this);
    }

    @Override
    public void attach(final @NotNull Container container, final boolean constraint) {
        getChildren().forEach(child -> child.attach(container, constraint));

        onAttached();
    }

    @Override
    public boolean attached() {
        return getChildren().allMatch(ComponentBase::attached);
    }

    @Override
    public @NotNull Container detach() {
        return getChildren()
                .map(ComponentBase::detach)
                .distinct()
                .findAny()
                .orElseThrow();
    }
}
