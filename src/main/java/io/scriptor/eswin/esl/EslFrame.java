package io.scriptor.eswin.esl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record EslFrame(@Nullable Object self, @Nullable Object event) {

    @Override
    public @NotNull Object self() {
        if (self == null)
            throw new IllegalStateException();
        return self;
    }

    @Override
    public @NotNull Object event() {
        if (event == null)
            throw new IllegalStateException();
        return event;
    }
}
