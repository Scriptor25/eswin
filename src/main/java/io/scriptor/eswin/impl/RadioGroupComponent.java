package io.scriptor.eswin.impl;

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
    private String selected;

    public RadioGroupComponent(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(parent, attributes, text);
    }

    @Override
    public void addListener(final @NotNull ActionListener listener) {
        listeners.add(listener);
    }

    public @NotNull String getSelected() {
        if (selected == null)
            throw new IllegalStateException();
        return selected;
    }

    public void setSelected(final @NotNull String id) {
        selected = id;
        System.out.println(id);

        getChildren().forEach(child -> {
            if (!child.getId().equals(id)) {
                child.getJRoot().setSelected(false);
                return;
            }

            final var event = new ActionEvent(child.getJRoot(), ActionEvent.ACTION_PERFORMED, "");
            listeners.forEach(listener -> listener.actionPerformed(event));

            child.getJRoot().setSelected(true);
        });
    }

    @Override
    public @NotNull Stream<RadioButtonComponent> getChildren() {
        return super.getChildren()
                    .filter(child -> child instanceof RadioButtonComponent)
                    .map(RadioButtonComponent.class::cast);
    }

    @Override
    public void chainInto(final @NotNull Container container, final boolean constraint) {
        getChildren().forEach(child -> child.chainInto(container, constraint));
    }

    @Override
    public void add(final @NotNull String id, final @NotNull ComponentBase child) {
        if (!(child instanceof RadioButtonComponent radio))
            return;

        super.add(id, child);

        if (selected == null)
            setSelected(id);

        radio.addListener(_ -> setSelected(id));
    }
}
