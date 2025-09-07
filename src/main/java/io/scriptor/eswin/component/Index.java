package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;

public record Index<T>(int index, T value) implements Comparable<Index<T>> {

    @Override
    public int compareTo(final @NotNull Index<T> other) {
        return Integer.compare(this.index, other.index);
    }
}
