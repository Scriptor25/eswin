package io.scriptor.eswin.context;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.xml.document.Attribute;
import io.scriptor.eswin.xml.document.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Context {

    private final Map<String, ComponentData> components = new HashMap<>();

    public Context() {
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
        return instantiate(null, name, new AttributeSet(), "");
    }

    public @NotNull ComponentBase instantiate(final @Nullable ComponentBase container, final @NotNull Element element) {
        final var instance = instantiate(container, element.name(), element.attributes(), element.text());

        element.elements()
               .map(e -> instantiate(container, e))
               .forEach(instance::put);

        return instance;
    }

    public @NotNull ComponentBase instantiate(
            final @Nullable ComponentBase container,
            final @NotNull String name,
            final @NotNull Attribute[] attributes,
            final @NotNull String text
    ) {
        final var set = new AttributeSet();
        for (final var attribute : attributes)
            set.put(attribute.name(), attribute.value());

        return instantiate(container, name, set, text);
    }

    public @NotNull ComponentBase instantiate(
            final @Nullable ComponentBase container,
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

            instance = constructor.newInstance(container, attributes, text);
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
