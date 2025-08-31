package io.scriptor.eswin.xml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Unroll extends Throwable {

    private static String substring(final @NotNull String string, int begin, int end) {
        begin = Math.max(begin, 0);
        end = Math.min(end, string.length());
        return string.substring(begin, end);
    }

    public final int mark;

    public Unroll(final int @NotNull [] codepoints, final int mark) {
        this(codepoints, mark, null);
    }

    public Unroll(final int @NotNull [] codepoints, final int mark, final @Nullable Throwable cause) {
        this(new String(codepoints, 0, codepoints.length), mark, cause);
    }

    public Unroll(final @NotNull String data, final int mark, final Throwable cause) {
        super("%s[%s]%s".formatted(
                      substring(data, mark - 5, mark),
                      substring(data, mark, mark + 1),
                      substring(data, mark + 1, mark + 5)),
              cause);
        this.mark = mark;
    }
}
