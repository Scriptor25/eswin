package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.*;
import io.scriptor.eswin.component.Component;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component("radio-group")
public class RadioGroupComponent extends ActionComponentBase<RadioGroupComponent, RadioGroupComponent.Payload> {

    public record Payload() {
    }

    private final List<ActionListener<RadioGroupComponent, Payload>> listeners = new ArrayList<>();

    public RadioGroupComponent(final @NotNull ComponentInfo info) {
        super(info);

        if (getAttributes().has("default"))
            setSelected(getAttributes().get("default"));
    }

    @Override
    public void addListener(final @NotNull ActionListener<RadioGroupComponent, Payload> listener) {
        listeners.add(listener);
    }

    public void setSelected(final @NotNull String id) {
        notify("selected", id);

        getRadioButtons().forEach(child -> {
            if (!child.getId().equals(id)) {
                child.setSelected(false);
                return;
            }

            final var event = new ActionEvent<>(this, new Payload());
            listeners.forEach(listener -> listener.callback(event));

            child.setSelected(true);
        });
    }

    public @NotNull Stream<RadioButtonComponent> getRadioButtons() {
        return getChildren()
                .filter(child -> child instanceof RadioButtonComponent)
                .map(RadioButtonComponent.class::cast);
    }

    @Override
    public void attach(final @NotNull Container container, final boolean constraint) {
        getChildren().forEach(child -> child.attach(container, constraint));

        onAttached();
    }

    @Override
    public boolean attached() {
        return getChildren().allMatch(ComponentBase::attached);
    }

    @Override
    public @NotNull Container detach() {
        return getChildren()
                .map(ComponentBase::detach)
                .distinct()
                .findAny()
                .orElseThrow();
    }

    @Override
    public void insert(final @NotNull String id, final @NotNull ComponentBase child) {
        super.insert(id, child);

        if (child instanceof RadioButtonComponent radio) {
            if (!has("selected"))
                setSelected(id);
            else
                radio.setSelected(id.equals(get("selected", "", String.class)));
            radio.addListener(_ -> setSelected(id));
        }
    }
}
