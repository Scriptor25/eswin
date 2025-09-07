package io.scriptor.eswin.util;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public final class Log {

    private Log() {
    }

    public static void info(final @NotNull String format, final @NotNull Object... args) {
        transform(args);

        System.err.printf("[info] %s%n", format.formatted(args));
    }

    public static void warn(final @NotNull String format, final @NotNull Object... args) {
        transform(args);

        System.err.printf("[warn] %s%n", format.formatted(args));
    }

    public static void transform(final @NotNull Object[] args) {
        for (int i = 0; i < args.length; ++i)
            if (args[i] instanceof Throwable thrown)
                args[i] = thrown(thrown);
    }

    public static @NotNull String thrown(final @NotNull Throwable thrown) {
        final var stream = new ByteArrayOutputStream();
        final var print  = new PrintStream(stream);
        thrown.printStackTrace(print);

        return stream.toString();
    }
}
