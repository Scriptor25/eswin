package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static io.scriptor.eswin.component.Constants.parseSwing;


@Component("radio-button")
public class RadioButtonComponent extends ActionComponentBase<RadioButtonComponent, RadioButtonComponent.Payload> {

    public record Payload() {
    }

    private final JRadioButton root;

    public RadioButtonComponent(
            final @NotNull ContextProvider provider,
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(provider, parent, attributes, text);

        apply(root = new JRadioButton());

        if (attributes.has("h-align"))
            root.setHorizontalAlignment(parseSwing(attributes.get("h-align")));

        if (attributes.has("v-align"))
            root.setVerticalAlignment(parseSwing(attributes.get("v-align")));

        observe("#text", root::setText, String.class);
    }

    @Override
    public void addListener(final @NotNull ActionListener<RadioButtonComponent, Payload> listener) {
        root.addActionListener(event -> {
            listener.callback(new ActionEvent<>(this, new Payload()));
        });
    }

    @Override
    public @NotNull JRadioButton getJRoot() {
        return root;
    }

    public void setSelected(final boolean selected) {
        root.setSelected(selected);
    }
}
