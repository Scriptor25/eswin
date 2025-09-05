package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.ActionComponentBase;
import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component("radio-group")
public class RadioGroupComponent extends ActionComponentBase {

    private final List<ActionListener> listeners = new ArrayList<>();

    public RadioGroupComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);

        if (attributes.has("default"))
            setSelected(attributes.get("default"));
    }

    @Override
    public void addListener(final @NotNull ActionListener listener) {
        listeners.add(listener);
    }

    public void setSelected(final @NotNull String id) {
        notify("selected", id);

        getRadioButtons().forEach(child -> {
            if (!child.getId().equals(id)) {
                child.setSelected(false);
                return;
            }

            final var event = new ActionEvent(child.getJRoot(), ActionEvent.ACTION_PERFORMED, "");
            listeners.forEach(listener -> listener.actionPerformed(event));

            child.setSelected(true);
        });
    }

    public @NotNull Stream<RadioButtonComponent> getRadioButtons() {
        return getChildren()
                .filter(child -> child instanceof RadioButtonComponent)
                .map(RadioButtonComponent.class::cast);
    }

    @Override
    public void render(final @NotNull Container container, final boolean constraint) {
        getChildren().forEach(child -> child.render(container, constraint));
    }

    @Override
    public void addChild(final @NotNull String id, final @NotNull ComponentBase child) {
        super.addChild(id, child);

        if (child instanceof RadioButtonComponent radio) {
            if (!has("selected"))
                setSelected(id);
            else
                radio.setSelected(id.equals(get("selected", "", String.class)));
            radio.addListener(_ -> setSelected(id));
        }
    }
}
