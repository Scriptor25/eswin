package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public record ConstantString(@NotNull String value) implements Constant {
}
