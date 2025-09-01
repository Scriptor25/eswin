package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public record ObjectValue(@NotNull Object value) implements Value {

    @Override
    public @NotNull Value field(final @NotNull String name) {
        final var cls = value.getClass();

        return new MemberValue(
                () -> {
                    final var mtd = cls.getMethod(name);
                    return mtd.invoke(value);
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
                    return mtd.invoke(value, arguments);
                });
    }
}
