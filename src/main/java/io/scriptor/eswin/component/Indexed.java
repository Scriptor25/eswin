package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;

public record Indexed<T>(int index, T value) implements Comparable<Indexed<T>> {

    @Override
    public int compareTo(final @NotNull Indexed<T> other) {
        return Integer.compare(this.index, other.index);
    }
}
