package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;

public record ActionEvent<C, P>(@NotNull C component, P payload) {
}
