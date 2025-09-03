package io.scriptor.eswin.grammar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class Grammar<R> {

    protected abstract R parseRoot(final @NotNull Context context) throws Unroll;

    public R parse(final @NotNull String filename, final int @NotNull [] buffer, final int offset, final int length) {
        final var context = new Context(filename, buffer, offset, length);
        return parse(context);
    }

    public R parse(final @NotNull String filename, final @NotNull String string, final int offset, final int length) {
        final var context = new Context(filename, string, offset, length);
        return parse(context);
    }

    public R parse(
            final @NotNull String filename,
            final @NotNull InputStream stream,
            final int offset,
            final int length
    ) throws IOException {
        final var context = new Context(filename, stream, offset, length);
        return parse(context);
    }

    public R parse(final @NotNull String filename, final int @NotNull [] buffer) {
        final var context = new Context(filename, buffer);
        return parse(context);
    }

    public R parse(final @NotNull String filename, final @NotNull String string) {
        final var context = new Context(filename, string);
        return parse(context);
    }

    public R parse(final @NotNull String filename, final @NotNull InputStream stream) throws IOException {
        final var context = new Context(filename, stream);
        return parse(context);
    }

    public R parse(final @NotNull Context context) {
        try {
            return parseRoot(context);
        } catch (final Unroll e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> @NotNull Rule<T> wrap(final @NotNull Rule<T> rule) {
        return context -> {
            final var mark = context.index();
            try {
                return rule.parse(context);
            } catch (final Unroll cause) {
                throw new Unroll(context, mark, cause);
            }
        };
    }

    public static <T> @NotNull Optional<T> parseZeroOrOneOf(
            final @NotNull Context context,
            final @NotNull Rule<T> rule
    ) {
        try {
            return Optional.of(rule.parse(context));
        } catch (final Unroll unroll) {
            context.index(unroll.index);
        }
        return Optional.empty();
    }

    public static <T> @NotNull Collection<T> parseOneOrMoreOf(
            final @NotNull Context context,
            final @NotNull Rule<T> rule
    ) throws Unroll {
        final List<T> data = new ArrayList<>();
        data.add(rule.parse(context));
        while (true)
            try {
                data.add(rule.parse(context));
            } catch (final Unroll unroll) {
                context.index(unroll.index);
                break;
            }
        return data;
    }

    public static <T> @NotNull Collection<T> parseZeroOrMoreOf(
            final @NotNull Context context,
            final @NotNull Rule<T> rule
    ) {
        final List<T> data = new ArrayList<>();
        while (true)
            try {
                data.add(rule.parse(context));
            } catch (final Unroll unroll) {
                context.index(unroll.index);
                break;
            }
        return data;
    }

    @SafeVarargs
    public static <T> T parseUnionOf(final @NotNull Context context, final @NotNull Rule<? extends T>... rules)
            throws Unroll {
        final var mark = context.index();

        for (final var rule : rules) {
            try {
                return rule.parse(context);
            } catch (final Unroll unroll) {
                context.index(unroll.index);
            }
        }

        throw new Unroll(context, mark);
    }
}
