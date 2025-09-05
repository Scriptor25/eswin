package io.scriptor.eswin.esl.runtime;

import io.scriptor.eswin.esl.EslCaller;
import io.scriptor.eswin.esl.EslGetter;
import io.scriptor.eswin.esl.EslSetter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public record MemberValue(@NotNull EslGetter getter, @NotNull EslSetter setter, @NotNull EslCaller caller)
        implements Value {

    @Override
    public @NotNull Object value() {
        try {
            return getter.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull Value store(final @NotNull Object value) {
        try {
            setter.set(value);
            return this;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull Value field(final @NotNull String name) {
        final var object = new ObjectValue(value());
        return object.field(name);
    }

    @Override
    public @NotNull Value call(final @NotNull Value[] arguments) {
        try {
            final var result = caller.call(Arrays.stream(arguments).map(Value::value).toArray());
            if (result == null)
                return new VoidValue();
            return new ObjectValue(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
