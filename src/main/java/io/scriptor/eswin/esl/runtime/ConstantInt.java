package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public record ConstantInt(@NotNull Long value) implements Constant {
}
