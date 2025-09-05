package io.scriptor.eswin.impl.example;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Component(value = "action-panel", layout = "layout/example/action.panel.xml")
public class ActionPanelComponent extends ComponentBase {

    public ActionPanelComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);
    }

    public void submit() {
    }

    public void cancel() {
    }

    public void help() {
    }

    public void advanced() {
    }

    public void options() {
    }
}
