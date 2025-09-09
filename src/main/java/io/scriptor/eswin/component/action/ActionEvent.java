package io.scriptor.eswin.component.action;

import org.jetbrains.annotations.NotNull;

public record ActionEvent<C extends ActionComponentBase<C, P>, P>(@NotNull C component, P payload) {
}
