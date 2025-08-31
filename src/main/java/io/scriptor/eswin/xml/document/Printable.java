package io.scriptor.eswin.xml.document;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public interface Printable {

    static @NotNull String spacing(final int depth) {
        final var codepoints = new int[depth * 2];
        Arrays.fill(codepoints, ' ');
        return new String(codepoints, 0, codepoints.length);
    }

    @NotNull String stringify(final int depth);
}
