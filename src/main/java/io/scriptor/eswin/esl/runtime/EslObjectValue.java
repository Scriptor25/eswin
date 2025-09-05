package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public record EslObjectValue<T>(@NotNull T value) implements EslValue<T> {

    @Override
    public @NotNull T value() {
        return value;
    }

    @Override
    public <V> @NotNull EslValue<V> field(final @NotNull String name, final @NotNull Class<V> type) {
        final var cls = value.getClass();

        return new EslMemberValue<>(
                () -> {
                    final var mtd = cls.getMethod(name);
                    return type.cast(mtd.invoke(value));
                },
                argument -> {
                    final var mtd = cls.getMethod(name, argument.getClass());
                    mtd.invoke(value, argument);
                },
                arguments -> {
                    final var types = new Class<?>[arguments.length];
                    for (int i = 0; i < arguments.length; ++i)
                        types[i] = arguments[i].getClass();
                    final var mtd = cls.getMethod(name, types);
                    return type.cast(mtd.invoke(value, arguments));
                });
    }
}
