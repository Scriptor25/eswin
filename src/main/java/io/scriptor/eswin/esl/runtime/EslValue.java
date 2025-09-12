package io.scriptor.eswin.esl.runtime;

import io.scriptor.eswin.util.Result;
import org.jetbrains.annotations.NotNull;

public interface EslValue<T> {

    default @NotNull Result<T> value() {
        throw new UnsupportedOperationException();
    }

    default @NotNull EslValue<T> store(final @NotNull T value) {
        throw new UnsupportedOperationException();
    }

    default <V> @NotNull EslValue<V> field(final @NotNull String name, final @NotNull Class<V> type) {
        throw new UnsupportedOperationException();
    }

    default <V> @NotNull EslValue<V> call(final @NotNull EslValue<?>[] arguments, final @NotNull Class<V> type) {
        throw new UnsupportedOperationException();
    }
}
