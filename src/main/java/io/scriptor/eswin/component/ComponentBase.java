package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.scriptor.eswin.component.Constants.*;

public abstract class ComponentBase {

    private final String id;
    private final ComponentBase parent;
    private final AttributeSet attributes;
    private final Map<String, Indexed<ComponentBase>> children = new HashMap<>();

    private final Map<String, Object> state = new HashMap<>();
    private final Map<String, List<Consumer<Object>>> observers = new HashMap<>();

    private ComponentBase root;

    public ComponentBase(
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        this.id = attributes.has("id")
                  ? attributes.get("id")
                  : UUID.randomUUID().toString();
        this.parent = parent;
        this.attributes = attributes;
    }

    public @NotNull String getId() {
        return id;
    }

    public @Nullable ComponentBase getContainer() {
        return parent;
    }

    public @NotNull AttributeSet getAttributes() {
        return attributes;
    }

    public void setRoot(final @NotNull ComponentBase root) {
        this.root = root;

        apply(this.root.getJRoot());
    }

    public @Nullable ComponentBase getRoot() {
        return root;
    }

    public @NotNull JComponent getJRoot() {
        if (root == null)
            throw new IllegalStateException();
        return root.getJRoot();
    }

    public void chainInto(final @NotNull Container container, final boolean constraint) {
        if (constraint) {
            container.add(getJRoot(), getConstraints());
            return;
        }
        container.add(getJRoot());
    }

    protected void apply(final @NotNull JComponent component) {
        if (attributes.has("align-x"))
            component.setAlignmentX(getAlignment(attributes.get("align-x")));
        if (attributes.has("align-y"))
            component.setAlignmentY(getAlignment(attributes.get("align-y")));
        if (attributes.has("tooltip"))
            component.setToolTipText(attributes.get("tooltip"));

        final var outside = attributes.has("border-title")
                            ? new TitledBorder(attributes.get("border-title"))
                            : null;

        final var borderInsets = new Insets(0, 0, 0, 0);
        final var inside = getInsets(attributes, "border-insets", borderInsets)
                           ? new EmptyBorder(borderInsets)
                           : null;

        if (outside != null || inside != null)
            component.setBorder(new CompoundBorder(outside, inside));

        component.setVisible(!attributes.has("hidden"));
    }

    public @NotNull GridBagConstraints getConstraints() {
        final var constraints = new GridBagConstraints();

        if (attributes.has("anchor"))
            constraints.anchor = getAnchor(attributes.get("anchor"));
        if (attributes.has("fill"))
            constraints.fill = getFill(attributes.get("fill"));
        if (attributes.has("grid-width"))
            constraints.gridwidth = getSize(attributes.get("grid-width"));
        if (attributes.has("grid-height"))
            constraints.gridheight = getSize(attributes.get("grid-height"));
        if (attributes.has("grid-x"))
            constraints.gridx = getSize(attributes.get("grid-x"));
        if (attributes.has("grid-y"))
            constraints.gridy = getSize(attributes.get("grid-y"));
        final var insets = new Insets(0, 0, 0, 0);
        if (getInsets(attributes, "insets", insets))
            constraints.insets = insets;
        if (attributes.has("pad-x"))
            constraints.ipadx = Integer.parseUnsignedInt(attributes.get("pad-x"), 10);
        if (attributes.has("pad-y"))
            constraints.ipady = Integer.parseUnsignedInt(attributes.get("pad-y"), 10);
        if (attributes.has("weight-x"))
            constraints.weightx = Double.parseDouble(attributes.get("weight-x"));
        if (attributes.has("weight-y"))
            constraints.weighty = Double.parseDouble(attributes.get("weight-y"));

        return constraints;
    }

    public @NotNull Stream<? extends ComponentBase> getChildren() {
        return children.values().stream().sorted().map(Indexed::value);
    }

    public @NotNull ComponentBase getFirst() {
        return getChildren().findFirst().orElseThrow(IllegalStateException::new);
    }

    public void add(final @NotNull String id, final @NotNull ComponentBase child) {
        if (children.containsKey(id))
            throw new IllegalStateException();

        children.put(id, new Indexed<>(children.size(), child));
    }

    public void add(final @NotNull ComponentBase child) {
        add(child.id, child);
    }

    public boolean has(final @NotNull String id) {
        if (children.containsKey(id))
            return true;
        for (final var child : children.values())
            if (child.value().has(id))
                return true;
        return false;
    }

    public <C extends ComponentBase> C get(final @NotNull String id, final @NotNull Class<C> type) {
        if (!children.containsKey(id)) {
            for (final var child : children.values())
                if (child.value().has(id))
                    return child.value().get(id, type);
            if (root != null)
                return root.get(id, type);
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

    public boolean isVisible() {
        return getJRoot().isVisible();
    }

    public void setVisible(final boolean visible) {
        getJRoot().setVisible(visible);
    }
}
