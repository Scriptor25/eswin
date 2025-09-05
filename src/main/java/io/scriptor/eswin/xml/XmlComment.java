package io.scriptor.eswin.xml;

import org.jetbrains.annotations.NotNull;

public record XmlComment(@NotNull String value) implements XmlBase {

    @Override
    public @NotNull String stringify(final int depth) {
        return "<!-- %s -->".formatted(value);
    }
}
