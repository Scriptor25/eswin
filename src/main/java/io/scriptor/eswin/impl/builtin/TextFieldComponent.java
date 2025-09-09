package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static io.scriptor.eswin.component.Constants.parseSwing;

@Component("text-field")
public class TextFieldComponent extends ActionComponentBase<TextFieldComponent, TextFieldComponent.Payload> {

    public record Payload() {
    }

    private final JTextField root;

    public TextFieldComponent(final @NotNull ComponentInfo info) {
        super(info.setUseText(true));

        apply(root = new JTextField());

        if (getAttributes().has("columns"))
            root.setColumns(Integer.parseUnsignedInt(getAttributes().get("columns"), 10));

        if (getAttributes().has("h-align"))
            root.setHorizontalAlignment(parseSwing(getAttributes().get("h-align")));

        if (getAttributes().has("default"))
            root.setText(getAttributes().get("default"));

        observe("#text", root::setText, String.class);
    }

    @Override
    public void addListener(final @NotNull ActionListener<TextFieldComponent, Payload> listener) {
        root.addActionListener(event -> {
            listener.callback(new ActionEvent<>(this, new Payload()));
        });
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return root;
    }

    public @NotNull String getText() {
        return root.getText();
    }
}
