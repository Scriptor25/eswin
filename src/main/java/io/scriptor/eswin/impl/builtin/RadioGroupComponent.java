package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import io.scriptor.eswin.component.action.ActionComponentBase;
import io.scriptor.eswin.component.action.ActionEvent;
import io.scriptor.eswin.component.action.ActionListener;
import io.scriptor.eswin.component.context.ContextProvider.ContextFrame;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component("radio-group")
public class RadioGroupComponent extends ActionComponentBase<RadioGroupComponent, RadioGroupComponent.Payload> {

    public record Payload() {
    }

    private final List<RadioButtonComponent> radioButtons = new ArrayList<>();
    private final List<ActionListener<RadioGroupComponent, Payload>> listeners = new ArrayList<>();

    private final RadioGroupContext context = new RadioGroupContext() {

        @Override
        public void addRadioButton(final @NotNull RadioButtonComponent radioButton) {
            radioButtons.add(radioButton);

            if (has("selected")) {
                final var selected = radioButton.getId().equals(get("selected", String.class));
                radioButton.getJRoot().setSelected(selected);
            } else {
                setSelected(radioButton.getId());
            }

            radioButton.addListener(_ -> setSelected(radioButton.getId()));
        }
    };

    private ContextFrame frame;

    public RadioGroupComponent(final @NotNull ComponentInfo info) {
        super(info);

        if (getAttributes().has("default"))
            setSelected(getAttributes().get("default"));
    }

    @Override
    protected void onBeginFrame() {
        frame = getProvider().provide(RadioGroupContext.class, context);
    }

    @Override
    protected void onEndFrame() {
        frame.close();
    }

    @Override
    public void addListener(final @NotNull ActionListener<RadioGroupComponent, Payload> listener) {
        listeners.add(listener);
    }

    public void setSelected(final @NotNull String id) {
        notify("selected", id);

        radioButtons.forEach(radioButton -> {
            if (!radioButton.getId().equals(id)) {
                radioButton.getJRoot().setSelected(false);
                return;
            }

            final var event = new ActionEvent<>(this, new Payload());
            listeners.forEach(listener -> listener.callback(event));

            radioButton.getJRoot().setSelected(true);
        });
    }
}
