package io.scriptor.eswin.esl;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface EslGetter {

    @NotNull Object get() throws Exception;
}
