package io.scriptor.eswin.util;

import org.jetbrains.annotations.NotNull;

public class MutableReference<T> implements Reference<T> {

    private T value;

    public MutableReference() {
    }

    public MutableReference(final @NotNull T value) {
        this.value = value;
    }

    @Override
    public boolean has() {
        return value != null;
    }

    @Override
    public @NotNull T get() {
        if (value == null)
            throw new IllegalStateException();
        return value;
    }

    public void set(final @NotNull T value) {
        if (this.value != null)
            throw new IllegalStateException();
        this.value = value;
    }
}
