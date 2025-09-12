package io.scriptor.eswin.component;

import io.scriptor.eswin.component.attribute.AttributeSet;
import io.scriptor.eswin.component.attribute.AttributeUtil;
import io.scriptor.eswin.component.context.ContextProvider;
import io.scriptor.eswin.util.Index;
import io.scriptor.eswin.util.Log;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.scriptor.eswin.component.attribute.AttributeUtil.*;
import static io.scriptor.eswin.util.ComponentEslUtil.observeText;

public abstract class ComponentBase {

    private final String id;
    private final ContextProvider provider;
    private final ComponentBase parent;
    private final AttributeSet attributes;
    private final Map<String, Index<ComponentBase>> children = new HashMap<>();

    private final Map<String, Object> state = new HashMap<>();
    private final Map<String, List<Consumer<?>>> observers = new HashMap<>();

    private ComponentBase root;
    private Container container;

    public ComponentBase(final @NotNull ComponentInfo info) {

        this.id = info.getAttributes().has("id")
                  ? info.getAttributes().get("id")
                  : UUID.randomUUID().toString();

        this.provider = info.getProvider();
        this.parent = info.hasParent() ? info.getParent() : this;
        this.attributes = info.getAttributes();

        if (info.observeText()) {
            observeText(getParent(), info.getText(), text -> notify("#text", text));
        }

        info.observedAttributes()
            .forEach(name -> observeText(
                    getParent(),
                    info.getAttributes().get(name),
                    text -> notify(name, text)));
    }

    protected void onBeginFrame() {
        Log.info("begin frame '%s' (%s)", id, getName());
    }

    protected void onEndFrame() {
        Log.info("end frame '%s' (%s)", id, getName());
    }

    protected void onAttached() {
        Log.info("attached '%s' (%s)", id, getName());
    }

    protected void onDetached() {
        Log.info("detached '%s' (%s)", id, getName());
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull ContextProvider getProvider() {
        return provider;
    }

    public @NotNull ComponentBase getParent() {
        return parent;
    }

    public @NotNull AttributeSet getAttributes() {
        return attributes;
    }

    public @NotNull String getName() {
        return getClass().getAnnotation(Component.class).value();
    }

    public void setRoot(final @NotNull ComponentBase root) {
        this.root = root;

        if (hasJRoot())
            apply(getJRoot());
    }

    public boolean hasRoot() {
        return root != null;
    }

    public @NotNull ComponentBase getRoot() {
        if (root == null)
            throw new IllegalStateException("component '%s' is missing the root component".formatted(getName()));
        return root;
    }

    public boolean hasJRoot() {
        return hasRoot() && getRoot().hasJRoot();
    }

    public @NotNull JComponent getJRoot() {
        return getRoot().getJRoot();
    }

    public void attach(final @NotNull Container container, final boolean constraint) {
        this.container = container;

        if (hasRoot()) {
            getRoot().attach(container, constraint, getConstraints());
        } else if (hasJRoot()) {
            if (constraint) {
                container.add(getJRoot(), getConstraints());
            } else {
                container.add(getJRoot());
            }

            getChildren().forEach(child -> child.attach(getJRoot(), true));
        } else {
            getChildren().forEach(child -> child.attach(container, constraint));
        }

        onAttached();
    }

    public void attach(
            final @NotNull Container container,
            final boolean constraint,
            final @NotNull GridBagConstraints constraints
    ) {
        if (hasRoot()) {
            getRoot().attach(container, constraint, constraints);

            onAttached();
            return;
        }

        this.container = container;

        if (hasJRoot()) {

            if (constraint) {
                container.add(getJRoot(), constraints);
            } else {
                container.add(getJRoot());
            }

            getChildren().forEach(child -> child.attach(getJRoot(), true));

            onAttached();
            return;
        }

        getChildren().forEach(child -> child.attach(container, constraint, constraints));
        onAttached();
    }

    public void notifyAttached() {
        getChildren().forEach(ComponentBase::notifyAttached);
        onAttached();
    }

    public @NotNull Container detach() {
        if (hasRoot()) {
            final var container = getRoot().detach();

            onDetached();
            return container;
        }

        final var container = this.container;
        this.container = null;

        if (container == null)
            throw new IllegalStateException("component '%s' is not attached to anything".formatted(getName()));

        getChildren().forEach(ComponentBase::detach);

        if (hasJRoot()) {
            container.remove(getJRoot());
        }

        onDetached();
        return container;
    }

    public void notifyDetached() {
        getChildren().forEach(ComponentBase::notifyDetached);
        onDetached();
    }

    protected void apply(final @NotNull JComponent component) {
        final var align = new Float[] { 0.0f, 0.0f };
        if (getComponents(attributes, "align", "align-x", "align-y", align, AttributeUtil::parseAlignment)) {
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

        if (attributes.has("background")) {
            final var value  = attributes.get("background");
            final var values = value.trim().split("\\s+");

            final Color color = switch (values.length) {
                case 1 -> {
                    if (!values[0].startsWith("#"))
                        throw new IllegalStateException();
                    final var string = values[0].substring(1);
                    yield switch (string.length()) {
                        case 3 -> {
                            final var r = string.charAt(0);
                            final var g = string.charAt(1);
                            final var b = string.charAt(2);
                            yield new Color(
                                    Integer.parseUnsignedInt("%1$c%1$c%2$c%2$c%3$c%3$c".formatted(r, g, b), 0x10),
                                    false);
                        }
                        case 6 -> new Color(Integer.parseUnsignedInt(string, 0x10), false);
                        case 8 -> new Color(Integer.parseUnsignedInt(string, 0x10), true);
                        default -> throw new IllegalStateException();
                    };
                }
                case 3 -> {
                    final var r = Integer.parseUnsignedInt(values[0], 10);
                    final var g = Integer.parseUnsignedInt(values[1], 10);
                    final var b = Integer.parseUnsignedInt(values[2], 10);
                    yield new Color(r, g, b);
                }
                case 4 -> {
                    final var r = Integer.parseUnsignedInt(values[0], 10);
                    final var g = Integer.parseUnsignedInt(values[1], 10);
                    final var b = Integer.parseUnsignedInt(values[2], 10);
                    final var a = Integer.parseUnsignedInt(values[3], 10);
                    yield new Color(r, g, b, a);
                }
                default -> throw new IllegalStateException();
            };
            component.setBackground(color);
            component.setOpaque(true);
        }

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
                          AttributeUtil::parseOffset,
                          AttributeUtil::parseSize)) {
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
        return children.values()
                       .stream()
                       .sorted()
                       .map(Index::value);
    }

    public void beginFrame() {
        onBeginFrame();
    }

    public void endFrame() {
        onEndFrame();
    }

    public void putChild(final @NotNull ComponentBase child) {
        if (children.containsKey(child.id))
            throw new IllegalStateException();

        children.put(child.id, new Index<>(children.size(), child));
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
        if (children.containsKey(id)) {
            final var child = children.get(id).value();

            if (!type.isInstance(child))
                throw new IllegalStateException("child with id '%s' is not an instance of type '%s'"
                                                        .formatted(id, type));
            return type.cast(child);
        }

        for (final var child : children.values())
            if (child.value().hasChild(id))
                return child.value().getChild(id, type);

        if (hasRoot())
            return getRoot().getChild(id, type);

        throw new IllegalStateException("no child with id '%s'".formatted(id));
    }

    public boolean has(final @NotNull String name) {
        return state.containsKey(name);
    }

    public <T> @NotNull T get(final @NotNull String name, final @NotNull T defaultValue, final @NotNull Class<T> type) {
        if (state.containsKey(name))
            return type.cast(state.get(name));
        return defaultValue;
    }

    public <T> @NotNull T get(final @NotNull String name, final @NotNull Class<T> type) {
        if (state.containsKey(name))
            return type.cast(state.get(name));
        throw new NoSuchElementException(name);
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

    @SuppressWarnings("unchecked")
    public <T> void notify(final @NotNull String name, final @NotNull T value) {
        state.put(name, value);

        if (!observers.containsKey(name))
            return;

        for (final var observer : observers.get(name))
            ((Consumer<T>) observer).accept(value);
    }
}
