package io.scriptor.eswin.xml;

import org.jetbrains.annotations.NotNull;

public record XmlDocument(@NotNull XmlInstruction[] instructions, @NotNull XmlElement root) implements XmlPrintable {

    @Override
    public @NotNull String toString() {
        return stringify(0);
    }

    @Override
    public @NotNull String stringify(final int depth) {
        final var builder = new StringBuilder();
        for (final var prologue : instructions) {
            builder.append(prologue.stringify(0))
                   .append('\n');
        }
        builder.append(root.stringify(0));
        return builder.toString();
    }
}
