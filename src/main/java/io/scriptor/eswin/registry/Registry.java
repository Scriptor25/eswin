package io.scriptor.eswin.registry;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.MutableAttributeSet;
import io.scriptor.eswin.xml.XmlAttribute;
import io.scriptor.eswin.xml.XmlElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Registry {

    private final Map<String, ComponentData> components = new HashMap<>();

    public Registry() {
    }

    public void put(final @NotNull String name, final @NotNull ComponentData component) {
        components.put(name, component);
    }

    public boolean has(final @NotNull String name) {
        return components.containsKey(name);
    }

    public @NotNull ComponentData get(final @NotNull String name) {
        if (!components.containsKey(name))
            throw new NoSuchElementException("no component with name '%s'".formatted(name));
        return components.get(name);
    }

    public @NotNull ComponentBase instantiate(final @NotNull String name) {
        return instantiate(null, name, new MutableAttributeSet(), "");
    }

    public @NotNull ComponentBase instantiate(
            final @Nullable ComponentBase parent,
            final @NotNull XmlElement element
    ) {
        final var instance = instantiate(parent, element.name(), element.attributes(), element.text());

        element.elements()
               .map(e -> instantiate(parent, e))
               .forEach(instance::add);

        return instance;
    }

    public @NotNull ComponentBase instantiate(
            final @Nullable ComponentBase parent,
            final @NotNull String name,
            final @NotNull XmlAttribute[] attributes,
            final @NotNull String text
    ) {
        final var set = new MutableAttributeSet();
        for (final var attribute : attributes)
            set.put(attribute.name(), attribute.value());

        return instantiate(parent, name, set, text);
    }

    public @NotNull ComponentBase instantiate(
            final @Nullable ComponentBase parent,
            final @NotNull String name,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        final var component = get(name);
        final var type      = component.type();
        final var layout    = component.layout();

        final ComponentBase instance;
        try {
            final var constructor = type.getConstructor(ComponentBase.class, AttributeSet.class, String.class);

            instance = constructor.newInstance(parent, attributes, text);
        } catch (final IllegalAccessException |
                       InstantiationException |
                       IllegalArgumentException |
                       NoSuchMethodException |
                       InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        if (layout != null)
            instance.setRoot(instantiate(instance, layout.root()));

        return instance;
    }
}
