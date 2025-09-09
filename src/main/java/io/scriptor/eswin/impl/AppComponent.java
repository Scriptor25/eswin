package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import org.jetbrains.annotations.NotNull;

@Component(
        value = "app",
        layout = "layout/app.xml"
)
public class AppComponent extends ComponentBase {

    public AppComponent(final @NotNull ComponentInfo info) {
        super(info);
    }
}
