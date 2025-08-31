package io.scriptor.eswin.xml.document;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public record Document(Collection<Instruction> instructions, Element root) implements Printable {

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
