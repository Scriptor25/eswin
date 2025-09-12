package io.scriptor.eswin.esl.runtime;

import io.scriptor.eswin.util.Result;
import org.jetbrains.annotations.NotNull;

public record EslObjectValue<T>(@NotNull T val) implements EslValue<T> {

    @Override
    public @NotNull Result<T> value() {
        return Result.ok(val);
    }

    @Override
    public <V> @NotNull EslValue<V> field(final @NotNull String name, final @NotNull Class<V> type) {
        final var cls = val.getClass();

        return new EslMemberValue<>(
                () -> {
                    final var mtd = cls.getMethod(name);
                    return type.cast(mtd.invoke(val));
                },
                argument -> {
                    final var mtd = cls.getMethod(name, argument.getClass());
                    mtd.invoke(val, argument);
                },
                arguments -> {
                    final var types = new Class<?>[arguments.length];
                    for (int i = 0; i < arguments.length; ++i)
                        types[i] = arguments[i].getClass();
                    final var mtd = cls.getMethod(name, types);
                    return type.cast(mtd.invoke(val, arguments));
                });
    }
}
