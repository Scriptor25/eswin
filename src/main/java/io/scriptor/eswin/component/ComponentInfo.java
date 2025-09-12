package io.scriptor.eswin.component;

import io.scriptor.eswin.component.attribute.AttributeSet;
import io.scriptor.eswin.component.context.ContextProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class ComponentInfo {

    private ContextProvider provider;
    private ComponentBase parent;
    private AttributeSet attributes;
    private String text = "";
    private boolean observeText = false;
    private final Set<String> observedAttributes = new HashSet<>();

    public @NotNull ContextProvider getProvider() {
        if (provider == null)
            throw new IllegalStateException();
        return provider;
    }

    public @NotNull ComponentInfo setProvider(final @NotNull ContextProvider provider) {
        this.provider = provider;
        return this;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public @NotNull ComponentBase getParent() {
        if (parent == null)
            throw new IllegalStateException();
        return parent;
    }

    public @NotNull ComponentInfo setParent(final @Nullable ComponentBase parent) {
        this.parent = parent;
        return this;
    }

    public @NotNull AttributeSet getAttributes() {
        if (attributes == null)
            throw new IllegalStateException();
        return attributes;
    }

    public @NotNull ComponentInfo setAttributes(final @NotNull AttributeSet attributes) {
        this.attributes = attributes;
        return this;
    }

    public @NotNull String getText() {
        return text;
    }

    public @NotNull ComponentInfo setText(final @NotNull String text) {
        this.text = text;
        return this;
    }

    public boolean observeText() {
        return observeText;
    }

    public @NotNull ComponentInfo setObserveText(final boolean observeText) {
        this.observeText = observeText;
        return this;
    }

    public @NotNull ComponentInfo addObservedAttribute(final @NotNull String name) {
        this.observedAttributes.add(name);
        return this;
    }

    public @NotNull Stream<String> observedAttributes() {
        return observedAttributes.stream();
    }
}
