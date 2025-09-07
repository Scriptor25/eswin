package io.scriptor.eswin.grammar;

import org.jetbrains.annotations.NotNull;

public class Unroll extends Throwable {

    private static String message(final @NotNull String filename, final int @NotNull [] codepoints, final int index) {
        int row = 1, col = 1;
        for (int i = 0; i < index; ++i)
            if (codepoints[i] == '\n') {
                row++;
                col = 1;
            } else {
                col++;
            }
        return "at %s:%d:%d".formatted(filename, row, col);
    }

    public final int index;

    public Unroll(final @NotNull GrammarContext context, final int index) {
        this(context.filename(), context.buffer(), index);
    }

    public Unroll(final @NotNull GrammarContext context, final int index, final @NotNull Throwable cause) {
        this(context.filename(), context.buffer(), index, cause);
    }

    public Unroll(final @NotNull String filename, final int @NotNull [] codepoints, final int index) {
        super(message(filename, codepoints, index));
        this.index = index;
    }

    public Unroll(
            final @NotNull String filename,
            final int @NotNull [] codepoints,
            final int index,
            final @NotNull Throwable cause
    ) {
        super(message(filename, codepoints, index), cause);
        this.index = index;
    }
}
