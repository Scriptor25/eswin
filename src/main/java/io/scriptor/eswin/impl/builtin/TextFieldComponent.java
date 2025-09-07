package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static io.scriptor.eswin.component.Constants.parseSwing;

@Component("text-field")
public class TextFieldComponent extends ActionComponentBase<TextFieldComponent, TextFieldComponent.Payload> {

    public record Payload() {
    }

    private final JTextField root;

    public TextFieldComponent(
            final @NotNull ContextProvider provider,
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(provider, parent, attributes, text);

        apply(root = new JTextField());

        if (attributes.has("columns"))
            root.setColumns(Integer.parseUnsignedInt(attributes.get("columns"), 10));

        if (attributes.has("h-align"))
            root.setHorizontalAlignment(parseSwing(attributes.get("h-align")));

        if (attributes.has("default"))
            root.setText(attributes.get("default"));

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
