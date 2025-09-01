package io.scriptor.eswin.esl;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface EslSetter {

    void set(final @NotNull Object value) throws Exception;
}
