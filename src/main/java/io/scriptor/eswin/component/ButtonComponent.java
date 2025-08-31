package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component("button")
public class ButtonComponent extends ComponentBase {

    private final JButton button;

    private static @NotNull ActionListener getAction(
            final @NotNull String action,
            final @Nullable ComponentBase container
    ) {
        final Class<?> loadedClass;
        final String   functionName;
        final Object   self;

        if (action.startsWith("this.")) {
            if (container == null)
                throw new IllegalStateException();

            self = container;

            loadedClass = container.getClass();
            functionName = action.substring(5);
        } else {
            self = null;

            final var functionIndex = action.lastIndexOf('.');
            final var className     = action.substring(0, functionIndex);
            functionName = action.substring(functionIndex + 1);

            try {
                loadedClass = ClassLoader.getSystemClassLoader().loadClass(className);
            } catch (final ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        final Method loadedFunction;
        try {
            loadedFunction = loadedClass.getMethod(functionName, ActionEvent.class);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return event -> {
            try {
                loadedFunction.invoke(self, event);
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public ButtonComponent(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(container, attributes, text);

        button = new JButton(text);

        if (attributes.has("tooltip"))
            button.setToolTipText(attributes.get("tooltip"));

        if (attributes.has("action"))
            button.addActionListener(getAction(attributes.get("action"), container));
    }

    @Override
    public @NotNull JButton getJRoot() {
        return button;
    }

    public @NotNull String getText() {
        return button.getText();
    }

    public void setText(final @NotNull String text) {
        button.setText(text);
    }

    public boolean isEnabled() {
        return button.isEnabled();
    }

    public void setEnabled(final boolean enabled) {
        button.setEnabled(enabled);
    }
}
