package io.scriptor.eswin.esl;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface EslGetter<T> {

    @NotNull T get() throws Exception;
}
