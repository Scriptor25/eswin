package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.scriptor.eswin.component.Constants.getAlignment;

public abstract class ComponentBase {

    private final String id;
    private final AttributeSet attributes;
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
        this.attributes = attributes;
    }

    public void setRoot(final @NotNull ComponentBase root) {
        this.root = root;

        if (this.root.hasJRoot())
            apply(this.root.getJRoot());
    }

    public @Nullable ComponentBase getRoot() {
        return root;
    }


    public boolean hasJRoot() {
        return root != null && root.hasJRoot();
    }

    public @NotNull JComponent getJRoot() {
        if (root == null)
            throw new IllegalStateException();
        return root.getJRoot();
    }

    protected void apply(final @NotNull JComponent component) {
        if (attributes.has("align-x")) {
            component.setAlignmentX(getAlignment(attributes.get("align-x")));
        }
        if (attributes.has("align-y")) {
            component.setAlignmentY(getAlignment(attributes.get("align-y")));
        }
        if (attributes.has("tooltip")) {
            component.setToolTipText(attributes.get("tooltip"));
        }
        component.setVisible(!attributes.has("hidden"));
    }

    public @NotNull Stream<ComponentBase> getChildren() {
        return children.values().stream();
    }

    public void putChild(final @NotNull String id, final @NotNull ComponentBase child) {
        children.put(id, child);

        if (Constants.DEBUG)
            System.out.printf("[%s] put %s -> %s%n", this.id, id, child);
    }

    public void putChild(final @NotNull ComponentBase child) {
        putChild(child.id, child);
    }

    public boolean hasChild(final @NotNull String id) {
        if (children.containsKey(id))
            return true;
        for (final var child : children.values())
            if (child.hasChild(id))
                return true;
        return false;
    }

    public <C extends ComponentBase> C getChild(final @NotNull String id, final @NotNull Class<C> type) {
        if (!children.containsKey(id)) {
            for (final var child : children.values())
                if (child.hasChild(id))
                    return child.getChild(id, type);
            if (root != null)
                return root.getChild(id, type);
            throw new IllegalStateException();
        }

        final var child = children.get(id);
        if (!type.isInstance(child))
            throw new IllegalStateException();

        return type.cast(child);
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

    public void chain(final @NotNull Container container) {
        if (root != null)
            root.chain(container);
        else
            container.add(getJRoot());
    }

    public boolean isVisible() {
        return hasJRoot() && getJRoot().isVisible();
    }

    public void setVisible(final boolean visible) {
        if (hasJRoot())
            getJRoot().setVisible(visible);
    }
}
