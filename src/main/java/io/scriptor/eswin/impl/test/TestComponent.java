package io.scriptor.eswin.impl.test;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.impl.builtin.EmbedComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Component(
        value = "test",
        layout = "layout/test/test.xml"
)
public class TestComponent extends ComponentBase {

    public TestComponent(
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
