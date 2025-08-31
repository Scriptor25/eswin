package io.scriptor.eswin.xml;

@FunctionalInterface
public interface Rule<R> {

    R parse() throws Unroll;
}
