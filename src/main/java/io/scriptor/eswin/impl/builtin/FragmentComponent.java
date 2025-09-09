package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@Component("fragment")
public class FragmentComponent extends ComponentBase {

    public FragmentComponent(final @NotNull ComponentInfo info) {
        super(info);
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
