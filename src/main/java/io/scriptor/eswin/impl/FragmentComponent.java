package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@Component("fragment")
public class FragmentComponent extends ComponentBase {

    public FragmentComponent(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);
    }

    @Override
    public void chain(final @NotNull Container container) {
        getChildren()
                .filter(ComponentBase::hasJRoot)
                .map(ComponentBase::getJRoot)
                .forEach(container::add);
    }
}
