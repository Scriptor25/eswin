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
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);
    }

    public void swap() {
        final var loremIpsum = getChild("lorem-ipsum", EmbedComponent.class);
        final var xmlDocs    = getChild("xml-docs", EmbedComponent.class);

        if (loremIpsum.isVisible()) {
            loremIpsum.setVisible(false);
            xmlDocs.setVisible(true);
        } else {
            loremIpsum.setVisible(true);
            xmlDocs.setVisible(false);
        }
    }
}
