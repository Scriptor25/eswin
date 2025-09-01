package io.scriptor.eswin.grammar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Unroll extends Throwable {

    private static String substring(final @NotNull String string, int begin, int end) {
        begin = Math.max(begin, 0);
        end = Math.min(end, string.length());
        return string.substring(begin, end);
    }

    public final int index;

    public Unroll(final @NotNull Context context, final int index) {
        this(context, index, null);
    }

    public Unroll(final @NotNull Context context, final int index, final @Nullable Throwable cause) {
        this(context.buffer(), index, cause);
    }

    public Unroll(final int @NotNull [] codepoints, final int index) {
        this(codepoints, index, null);
    }

    public Unroll(final int @NotNull [] codepoints, final int index, final @Nullable Throwable cause) {
        this(new String(codepoints, 0, codepoints.length), index, cause);
    }

    public Unroll(final @NotNull String data, final int index, final Throwable cause) {
        super("%s[%s]%s".formatted(
                      substring(data, index - 5, index),
                      substring(data, index, index + 1),
                      substring(data, index + 1, index + 5)),
              cause);
        this.index = index;
    }
}
