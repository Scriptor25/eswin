package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public record ConstantPackage(@NotNull String path) implements Constant {

    @Override
    public @NotNull Object value() {
        throw new IllegalStateException();
    }

    @Override
    public @NotNull Value field(final @NotNull String name) {
        return new ConstantPackage(path + '.' + name);
    }
}
