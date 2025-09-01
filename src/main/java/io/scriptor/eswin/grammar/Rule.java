package io.scriptor.eswin.grammar;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Rule<R> {

    R parse(final @NotNull Context context) throws Unroll;
}
