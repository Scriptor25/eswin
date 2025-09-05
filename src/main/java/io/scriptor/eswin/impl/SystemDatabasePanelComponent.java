package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Component(value = "system-database-panel", layout = "layout/system.database.panel.xml")
public class SystemDatabasePanelComponent extends ComponentBase {

    public SystemDatabasePanelComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);
    }

    public void select() {
        final var component = getChild("selection", RadioGroupComponent.class);
        final var selected  = component.get("selected", "", String.class);
    }
}
