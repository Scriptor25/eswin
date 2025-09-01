package io.scriptor.eswin.xml;

import org.jetbrains.annotations.NotNull;

public record XmlText(String value) implements XmlBase {

    @Override
    public @NotNull String toString() {
        return stringify(0);
    }

    @Override
    public @NotNull String stringify(final int depth) {
        return value;
    }
}
