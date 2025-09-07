package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ContextProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Component(
        value = "app",
        layout = "layout/app.xml"
)
public class AppComponent extends ComponentBase {

    public AppComponent(
            final @NotNull ContextProvider provider,
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(provider, parent, attributes, text);
    }
}
