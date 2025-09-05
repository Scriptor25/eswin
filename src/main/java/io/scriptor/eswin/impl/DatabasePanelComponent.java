package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Component(value = "database-panel", layout = "layout/database.panel.xml")
public class DatabasePanelComponent extends ComponentBase {

    public DatabasePanelComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);
    }

    public void select() {
    }

    public void create() {
    }

    public void repair() {
    }

    public void compact() {
    }
}
