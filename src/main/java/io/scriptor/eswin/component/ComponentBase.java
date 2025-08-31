package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.function.Consumer;

public abstract class ComponentBase {

    private final String id;
    private final Map<String, ComponentBase> children = new HashMap<>();

    private final Map<String, Object> state = new HashMap<>();
    private final Map<String, List<Consumer<Object>>> observers = new HashMap<>();

    private ComponentBase root;

    public ComponentBase(
            final @Nullable ComponentBase container,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        this.id = attributes.has("id")
                  ? attributes.get("id")
                  : UUID.randomUUID().toString();
    }

    public void setRoot(final @NotNull ComponentBase root) {
        this.root = root;
    }

    public @Nullable JComponent getJRoot() {
        if (root == null)
            return null;
        return root.getJRoot();
    }

    public void put(final @NotNull String id, final @NotNull ComponentBase component) {
        children.put(id, component);

        if (Constants.DEBUG)
            System.out.printf("[%s] put %s -> %s%n", this.id, id, component);
    }

    public void put(final @NotNull ComponentBase component) {
        put(component.id, component);
    }

    public boolean has(final @NotNull String id) {
        if (children.containsKey(id))
            return true;
        for (final var child : children.values())
            if (child.has(id))
                return true;
        return false;
    }

    public <C extends ComponentBase> C get(final @NotNull String id, final @NotNull Class<C> type) {
        if (!children.containsKey(id)) {
            for (final var child : children.values())
                if (child.has(id))
                    return child.get(id, type);
            if (root != null)
                return root.get(id, type);
            throw new IllegalStateException();
        }

        final var component = children.get(id);
        if (!type.isInstance(component))
            throw new IllegalStateException();

        return type.cast(component);
    }

    public void observe(final @NotNull String name, final @NotNull Consumer<Object> observer) {
        if (state.containsKey(name))
            observer.accept(state.get(name));

        observers.computeIfAbsent(name, _ -> new ArrayList<>()).add(observer);
    }

    public void notify(final @NotNull String name, final @NotNull Object value) {
        state.put(name, value);

        if (!observers.containsKey(name))
            return;

        for (final var observer : observers.get(name))
            observer.accept(value);
    }
}
