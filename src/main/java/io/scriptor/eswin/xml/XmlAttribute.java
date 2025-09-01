package io.scriptor.eswin.xml;

import org.jetbrains.annotations.NotNull;

public record XmlAttribute(String name, String value) implements XmlPrintable {

    @Override
    public @NotNull String toString() {
        return stringify(0);
    }

    @Override
    public @NotNull String stringify(final int depth) {
        return "%s=\"%s\"".formatted(name, value);
    }
}
