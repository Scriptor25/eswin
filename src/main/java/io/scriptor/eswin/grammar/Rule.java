package io.scriptor.eswin.grammar;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Rule<R> {

    R parse(final @NotNull GrammarContext context) throws Unroll;
}
