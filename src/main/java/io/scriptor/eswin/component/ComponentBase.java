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
import static io.scriptor.eswin.util.EslUtil.getSegments;
import static io.scriptor.eswin.util.EslUtil.observeSegments;

public abstract class ComponentBase {

    private final String id;
    private final ComponentBase parent;
    private final AttributeSet attributes;
    private final Map<String, Indexed<ComponentBase>> children = new HashMap<>();

    private final String[] segments;

    private final Map<String, Object> state = new HashMap<>();
    private final Map<String, List<Consumer<?>>> observers = new HashMap<>();

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

        final var expressions = getSegments(text);
        this.segments = new String[expressions.length];
        Arrays.fill(this.segments, "");

        observeSegments(parent, expressions, (index, value) -> {
            this.segments[index] = value.toString();
            notify("#text", String.join("", this.segments));
        });
    }

    public @NotNull String getId() {
        return id;
    }

    public @Nullable ComponentBase getParent() {
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

    public void render(final @NotNull Container container, final boolean constraint) {
        if (constraint) {
            container.add(getJRoot(), getConstraints());
            return;
        }
        container.add(getJRoot());
    }

    protected void apply(final @NotNull JComponent component) {
        final var align = new Float[] { 0.0f, 0.0f };
        if (getComponents(attributes, "align", "align-x", "align-y", align, Constants::parseAlignment)) {
            component.setAlignmentX(align[0]);
            component.setAlignmentY(align[1]);
        }

        if (attributes.has("tooltip")) {
            component.setToolTipText(attributes.get("tooltip"));
        }

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

        if (attributes.has("anchor")) {
            constraints.anchor = parseAnchor(attributes.get("anchor"));
        }

        if (attributes.has("fill")) {
            constraints.fill = parseFill(attributes.get("fill"));
        }

        final var grid = new Integer[] { GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, 1, 1 };
        if (getComponents(attributes,
                          "grid",
                          "grid-offset",
                          "grid-size",
                          "grid-x",
                          "grid-y",
                          "grid-width",
                          "grid-height",
                          grid,
                          Constants::parseOffset,
                          Constants::parseSize)) {
            constraints.gridx = grid[0];
            constraints.gridy = grid[1];
            constraints.gridwidth = grid[2];
            constraints.gridheight = grid[3];
        }

        final var insets = new Insets(0, 0, 0, 0);
        if (getInsets(attributes, "insets", insets)) {
            constraints.insets = insets;
        }

        final var pad = new Integer[] { 0, 0 };
        if (getComponents(attributes, "pad", "pad-x", "pad-y", pad, Integer::parseUnsignedInt)) {
            constraints.ipadx = pad[0];
            constraints.ipady = pad[1];
        }

        final var weight = new Double[] { 0.0, 0.0 };
        if (getComponents(attributes, "weight", "weight-x", "weight-y", weight, Double::parseDouble)) {
            constraints.weightx = weight[0];
            constraints.weighty = weight[1];
        }

        return constraints;
    }

    public @NotNull Stream<? extends ComponentBase> getChildren() {
        return children.values().stream().sorted().map(Indexed::value);
    }

    public void addChild(final @NotNull String id, final @NotNull ComponentBase child) {
        if (children.containsKey(id))
            throw new IllegalStateException();

        children.put(id, new Indexed<>(children.size(), child));
    }

    public void addChild(final @NotNull ComponentBase child) {
        addChild(child.id, child);
    }

    public boolean hasChild(final @NotNull String id) {
        if (children.containsKey(id))
            return true;
        for (final var child : children.values())
            if (child.value().hasChild(id))
                return true;
        return false;
    }

    public <C extends ComponentBase> C getChild(final @NotNull String id, final @NotNull Class<C> type) {
        if (!children.containsKey(id)) {
            for (final var child : children.values())
                if (child.value().hasChild(id))
                    return child.value().getChild(id, type);
            if (root != null)
                return root.getChild(id, type);
            throw new IllegalStateException("no child with id '%s'".formatted(id));
        }

        final var child = children.get(id).value();
        if (!type.isInstance(child))
            throw new IllegalStateException("child with id '%s' is not an instance of type '%s'".formatted(id, type));

        return type.cast(child);
    }

    public boolean has(final @NotNull String name) {
        return state.containsKey(name);
    }

    public <T> @NotNull T get(final @NotNull String name, final @NotNull T defaultValue, final @NotNull Class<T> type) {
        if (state.containsKey(name))
            return type.cast(state.get(name));
        return defaultValue;
    }

    public <T> void observe(
            final @NotNull String name,
            final @NotNull Consumer<T> observer,
            final @NotNull Class<T> type
    ) {
        if (state.containsKey(name))
            observer.accept(type.cast(state.get(name)));

        observers.computeIfAbsent(name, _ -> new ArrayList<>()).add(observer);
    }

    public <T> void notify(final @NotNull String name, final @NotNull T value) {
        state.put(name, value);

        if (!observers.containsKey(name))
            return;

        for (final var observer : observers.get(name))
            ((Consumer<T>) observer).accept(value);
    }

    public boolean isVisible() {
        return getJRoot().isVisible();
    }

    public void setVisible(final boolean visible) {
        getJRoot().setVisible(visible);
    }
}
