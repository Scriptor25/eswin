package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ComponentInfo {

    private ContextProvider provider;
    private ComponentBase parent;
    private AttributeSet attributes;
    private String text = "";
    private boolean useText = false;

    public @NotNull ContextProvider getProvider() {
        if (provider == null)
            throw new IllegalStateException();
        return provider;
    }

    public @NotNull ComponentInfo setProvider(final @NotNull ContextProvider provider) {
        this.provider = provider;
        return this;
    }

    public @Nullable ComponentBase getParent() {
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

    public boolean useText() {
        return useText;
    }

    public @NotNull ComponentInfo setUseText(final boolean useText) {
        this.useText = useText;
        return this;
    }
}
