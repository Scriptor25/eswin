package io.scriptor.eswin.util;

import org.jetbrains.annotations.NotNull;

public final class Usable<T> {

    private boolean used;
    private final T value;

    public Usable(final @NotNull T value) {
        this.used = false;
        this.value = value;
    }

    public boolean used() {
        return used;
    }


    public @NotNull T get() {
        used = true;
        return value;
    }
}
