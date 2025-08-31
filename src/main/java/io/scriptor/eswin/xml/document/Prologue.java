package io.scriptor.eswin.xml.document;

import org.jetbrains.annotations.NotNull;

public record Prologue(String name, Attribute[] attributes) implements Printable {

    @Override
    public @NotNull String toString() {
        return stringify(0);
    }

    @Override
    public @NotNull String stringify(final int depth) {
        final var builder = new StringBuilder();
        builder.append("<?").append(name);
        for (final var attribute : attributes) {
            builder.append(' ').append(attribute.stringify(0));
        }
        builder.append(" ?>");
        return builder.toString();
    }
}
