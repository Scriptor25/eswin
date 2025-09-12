package io.scriptor.eswin.esl.runtime;

import io.scriptor.eswin.util.Result;
import org.jetbrains.annotations.NotNull;

public record EslErrorValue<T>(@NotNull Throwable thrown) implements EslValue<T> {

    @Override
    public @NotNull Result<T> value() {
        return Result.error(thrown);
    }

    @Override
    public @NotNull EslValue<T> store(final @NotNull T value) {
        return new EslErrorValue<>(thrown);
    }

    @Override
    public @NotNull <V> EslValue<V> field(final @NotNull String name, final @NotNull Class<V> type) {
        return new EslErrorValue<>(thrown);
    }

    @Override
    public @NotNull <V> EslValue<V> call(final @NotNull EslValue<?>[] arguments, final @NotNull Class<V> type) {
        return new EslErrorValue<>(thrown);
    }
}
