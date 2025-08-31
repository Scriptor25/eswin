package io.scriptor.eswin.xml.document;

import org.jetbrains.annotations.NotNull;

public record Text(String value) implements ElementBase {

    @Override
    public @NotNull String toString() {
        return stringify(0);
    }

    @Override
    public @NotNull String stringify(final int depth) {
        return value;
    }
}
