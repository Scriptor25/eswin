package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@Component("fragment")
public class FragmentComponent extends ComponentBase {

    public FragmentComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);
    }

    @Override
    public void render(final @NotNull Container container, final boolean constraint) {
        getChildren().forEach(child -> child.render(container, constraint));
    }
}
