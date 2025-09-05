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
        final var loremIpsum = get("lorem-ipsum", EmbedComponent.class);
        final var xmlDocs    = get("xml-docs", EmbedComponent.class);

        if (loremIpsum.isVisible()) {
            loremIpsum.setVisible(false);
            xmlDocs.setVisible(true);
        } else {
            loremIpsum.setVisible(true);
            xmlDocs.setVisible(false);
        }
    }
}
