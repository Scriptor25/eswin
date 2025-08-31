package io.scriptor.eswin;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class Context {

    private final Map<String, JComponent> components = new HashMap<>();

    public Context() {
    }

    public void put(final @NotNull String name, final @NotNull JComponent component) {
        components.put(name, component);
    }

    public boolean has(final @NotNull String name) {
        return components.containsKey(name);
    }

    public <C extends JComponent> @NotNull C get(final @NotNull String name, final @NotNull Class<C> type) {
        if (!components.containsKey(name))
            throw new IllegalStateException();
        return type.cast(components.get(name));
    }
}
