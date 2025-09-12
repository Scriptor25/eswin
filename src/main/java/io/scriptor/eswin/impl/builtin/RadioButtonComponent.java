package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentInfo;
import io.scriptor.eswin.component.action.ActionComponentBase;
import io.scriptor.eswin.component.action.ActionEvent;
import io.scriptor.eswin.component.action.ActionListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static io.scriptor.eswin.component.attribute.AttributeUtil.parseSwing;


@Component("radio-button")
public class RadioButtonComponent extends ActionComponentBase<RadioButtonComponent, RadioButtonComponent.Payload> {

    public record Payload() {
    }

    private final JRadioButton root;

    public RadioButtonComponent(final @NotNull ComponentInfo info) {
        super(info.setObserveText(true));

        apply(root = new JRadioButton());

        if (getAttributes().has("h-align"))
            root.setHorizontalAlignment(parseSwing(getAttributes().get("h-align")));

        if (getAttributes().has("v-align"))
            root.setVerticalAlignment(parseSwing(getAttributes().get("v-align")));

        observe("#text", root::setText, String.class);

        if (getProvider().provides(RadioGroupContext.class)) {
            final var context = getProvider().use(RadioGroupContext.class);
            context.addRadioButton(this);
        }
    }

    @Override
    public void addListener(final @NotNull ActionListener<RadioButtonComponent, Payload> listener) {
        root.addActionListener(event -> {
            listener.callback(new ActionEvent<>(this, new Payload()));
        });
    }

    @Override
    public boolean hasJRoot() {
        return true;
    }

    @Override
    public @NotNull JRadioButton getJRoot() {
        return root;
    }
}
