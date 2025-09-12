package io.scriptor.eswin.esl.runtime;

import io.scriptor.eswin.esl.EslCaller;
import io.scriptor.eswin.esl.EslGetter;
import io.scriptor.eswin.esl.EslSetter;
import io.scriptor.eswin.util.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public record EslMemberValue<T>(
        @NotNull EslGetter<T> getter,
        @NotNull EslSetter<T> setter,
        @NotNull EslCaller<T> caller
)
        implements EslValue<T> {

    @Override
    public @NotNull Result<T> value() {
        try {
            return Result.ok(getter.get());
        } catch (final Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public @NotNull EslValue<T> store(final @NotNull T value) {
        try {
            setter.set(value);
            return this;
        } catch (final Exception e) {
            return new EslErrorValue<>(e);
        }
    }

    @Override
    public <V> @NotNull EslValue<V> field(final @NotNull String name, final @NotNull Class<V> type) {
        final var object = new EslObjectValue<>(value());
        return object.field(name, type);
    }

    @Override
    public <V> @NotNull EslValue<V> call(final @NotNull EslValue<?>[] arguments, final @NotNull Class<V> type) {
        try {
            final var result = caller.call(Arrays.stream(arguments)
                                                 .map(EslValue::value)
                                                 .toArray());
            if (result == null)
                return new EslVoidValue<>();

            return new EslObjectValue<>(type.cast(result));
        } catch (final Exception e) {
            return new EslErrorValue<>(e);
        }
    }
}
