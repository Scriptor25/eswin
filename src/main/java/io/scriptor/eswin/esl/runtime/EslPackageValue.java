package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public record EslPackageValue<T>(@NotNull String name) implements EslValue<T> {

    @Override
    public <V> @NotNull EslValue<V> field(final @NotNull String name, final @NotNull Class<V> type) {
        return new EslPackageValue<>(this.name + '.' + name);
    }

    @Override
    public <V> @NotNull EslValue<V> call(final @NotNull EslValue<?>[] arguments, final @NotNull Class<V> type) {
        final var split   = name.lastIndexOf('.');
        final var clsName = name.substring(0, split);
        final var mtdName = name.substring(split + 1);

        final var values = new Object[arguments.length];
        for (int i = 0; i < arguments.length; ++i)
            values[i] = arguments[i].value();

        final var types = new Class<?>[values.length];
        for (int i = 0; i < values.length; ++i)
            types[i] = values[i].getClass();

        try {
            final var cls = ClassLoader.getSystemClassLoader().loadClass(clsName);
            final var mtd = cls.getMethod(mtdName, types);

            final var result = mtd.invoke(null, values);
            if (result == null)
                return new EslVoidValue<>();
            return new EslObjectValue<>(type.cast(result));
        } catch (final ClassNotFoundException |
                       InvocationTargetException |
                       NoSuchMethodException |
                       IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
