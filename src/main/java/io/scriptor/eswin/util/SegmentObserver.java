package io.scriptor.eswin.util;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SegmentObserver {

    void notify(final int index, final @NotNull Object value);
}
