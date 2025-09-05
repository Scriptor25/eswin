package io.scriptor.eswin.esl;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface EslSetter<T> {

    void set(final @NotNull T value) throws Exception;
}
