package io.scriptor.eswin.impl.example;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.impl.builtin.TextFieldComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Component(value = "source-panel", layout = "layout/example/source.panel.xml")
public class SourcePanelComponent extends ComponentBase {

    public SourcePanelComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);
    }

    public void name() {
        final var component = getChild("name", TextFieldComponent.class);
        final var text      = component.getText();
        notify("name", text);
    }

    public void description() {
        final var component = getChild("description", TextFieldComponent.class);
        final var text      = component.getText();
        notify("description", text);
    }
}
