package io.scriptor.eswin.util;

import org.jetbrains.annotations.NotNull;

public interface Reference<T> {

    boolean has();

    @NotNull T get();
}
