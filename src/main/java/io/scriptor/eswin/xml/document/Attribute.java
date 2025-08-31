package io.scriptor.eswin.xml.document;

import org.jetbrains.annotations.NotNull;

public record Attribute(String name, String value) implements Printable {

    @Override
    public @NotNull String toString() {
        return stringify(0);
    }

    @Override
    public @NotNull String stringify(final int depth) {
        return "%s=\"%s\"".formatted(name, value);
    }
}
