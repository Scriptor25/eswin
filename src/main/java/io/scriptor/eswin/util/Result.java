package io.scriptor.eswin.util;

import org.jetbrains.annotations.NotNull;

public final class Result<T> {

    public static <T> Result<T> ok(final @NotNull T value) {
        return new Result<>(value);
    }

    public static <T> Result<T> error(final @NotNull String format, final @NotNull Object... args) {
        return new Result<>(new Throwable(format.formatted(args)));
    }

    public static <T> Result<T> error(final @NotNull Throwable thrown) {
        return new Result<>(thrown);
    }

    private final T value;
    private final Throwable thrown;

    private Result(final @NotNull T value) {
        this.value = value;
        this.thrown = null;
    }

    private Result(final @NotNull Throwable thrown) {
        this.value = null;
        this.thrown = thrown;
    }

    public @NotNull T value() {
        if (value == null || thrown != null)
            throw new IllegalStateException(thrown);
        return value;
    }

    public @NotNull Throwable thrown() {
        if (value == null && thrown != null)
            return thrown;
        throw new IllegalStateException(thrown);
    }

    public boolean error() {
        return value == null && thrown != null;
    }
}
