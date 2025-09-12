package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentInfo;
import io.scriptor.eswin.component.action.ActionComponentBase;
import io.scriptor.eswin.component.action.ActionEvent;
import io.scriptor.eswin.component.action.ActionListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

@Component("button")
public class ButtonComponent extends ActionComponentBase<ButtonComponent, ButtonComponent.Payload> {

    public record Payload() {
    }

    private final JButton root;

    public ButtonComponent(final @NotNull ComponentInfo info) {
        super(info.setObserveText(true));

        apply(root = new JButton());
        root.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        observe("#text", root::setText, String.class);
    }

    @Override
    public void addListener(final @NotNull ActionListener<ButtonComponent, Payload> listener) {
        root.addActionListener(event -> {
            listener.callback(new ActionEvent<>(this, new Payload()));
        });
    }

    @Override
    public boolean hasJRoot() {
        return true;
    }

    @Override
    public @NotNull JComponent getJRoot() {
        return root;
    }

    public boolean isEnabled() {
        return root.isEnabled();
    }

    public void setEnabled(final boolean enabled) {
        root.setEnabled(enabled);
    }
}
