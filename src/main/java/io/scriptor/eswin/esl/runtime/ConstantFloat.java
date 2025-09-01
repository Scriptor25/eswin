package io.scriptor.eswin.esl.runtime;

import org.jetbrains.annotations.NotNull;

public record ConstantFloat(@NotNull Double value) implements Constant {
}
