package io.scriptor.eswin.registry;

import io.scriptor.eswin.component.*;
import io.scriptor.eswin.util.Log;
import io.scriptor.eswin.xml.XmlAttribute;
import io.scriptor.eswin.xml.XmlElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    public @NotNull Optional<ComponentBase> instantiate(
            final @NotNull ContextProvider provider,
            final @NotNull String name
    ) {
        return instantiate(provider, null, name, new MutableAttributeSet(), "");
    }

    public @NotNull Optional<ComponentBase> instantiate(
            final @NotNull ContextProvider provider,
            final @Nullable ComponentBase parent,
            final @NotNull XmlElement element
    ) {
        return instantiate(provider, parent, element.name(), element.attributes(), element.text())
                .map(instance -> {
                    instance.beginFrame();
                    element.elements()
                           .map(e -> instantiate(provider, parent, e))
                           .filter(Optional::isPresent)
                           .map(Optional::get)
                           .forEach(instance::insert);
                    instance.endFrame();

                    return instance;
                });
    }

    public @NotNull Optional<ComponentBase> instantiate(
            final @NotNull ContextProvider provider,
            final @Nullable ComponentBase parent,
            final @NotNull String name,
            final @NotNull XmlAttribute[] attributes,
            final @NotNull String text
    ) {
        final var set = new MutableAttributeSet();
        for (final var attribute : attributes)
            set.put(attribute.name(), attribute.value());

        return instantiate(provider, parent, name, set, text);
    }

    public @NotNull Optional<ComponentBase> instantiate(
            final @NotNull ContextProvider provider,
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
            final var constructor = type.getConstructor(ComponentInfo.class);

            final var info = new ComponentInfo()
                    .setProvider(provider)
                    .setParent(parent)
                    .setAttributes(attributes)
                    .setText(text);

            instance = constructor.newInstance(info);
        } catch (final IllegalAccessException |
                       InstantiationException |
                       IllegalArgumentException |
                       NoSuchMethodException |
                       InvocationTargetException e) {
            Log.warn("when instantiating component '%s': %s", name, e);
            return Optional.empty();
        }

        if (layout != null)
            instantiate(provider, instance, layout.root()).ifPresent(instance::setRoot);

        return Optional.of(instance);
    }
}
