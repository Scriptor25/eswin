package io.scriptor.eswin.util;

import org.jetbrains.annotations.NotNull;

public final class Log {

    private Log() {
    }

    public static void info(final @NotNull String format, final @NotNull Object... args) {
        System.err.printf("[info] %s%n", format.formatted(args));
    }

    public static void warn(final @NotNull String format, final @NotNull Object... args) {
        System.err.printf("[warn] %s%n", format.formatted(args));
    }
}
