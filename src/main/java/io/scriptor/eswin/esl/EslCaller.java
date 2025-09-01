package io.scriptor.eswin.esl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface EslCaller {

    @Nullable Object call(final @NotNull Object[] arguments) throws Exception;
}
