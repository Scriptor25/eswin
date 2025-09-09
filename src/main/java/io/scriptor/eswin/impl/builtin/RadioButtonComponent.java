package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static io.scriptor.eswin.component.Constants.parseSwing;


@Component("radio-button")
public class RadioButtonComponent extends ActionComponentBase<RadioButtonComponent, RadioButtonComponent.Payload> {

    public record Payload() {
    }

    private final JRadioButton root;

    public RadioButtonComponent(final @NotNull ComponentInfo info) {
        super(info.setUseText(true));

        apply(root = new JRadioButton());

        if (getAttributes().has("h-align"))
            root.setHorizontalAlignment(parseSwing(getAttributes().get("h-align")));

        if (getAttributes().has("v-align"))
            root.setVerticalAlignment(parseSwing(getAttributes().get("v-align")));

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
