package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Component(
        value = "app",
        layout = "layout/app.xml"
)
public class AppComponent extends ComponentBase {

    public AppComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);
    }

    public void swap() {
        final var embed1 = getChild("embed1", EmbedComponent.class);
        final var embed2 = getChild("embed2", EmbedComponent.class);

        if (embed1.isVisible()) {
            embed1.setVisible(false);
            embed2.setVisible(true);
        } else {
            embed1.setVisible(true);
            embed2.setVisible(false);
        }
    }
}
