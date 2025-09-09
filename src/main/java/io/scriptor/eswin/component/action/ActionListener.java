package io.scriptor.eswin.component.action;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ActionListener<C extends ActionComponentBase<C, P>, P> {

    void callback(final @NotNull ActionEvent<C, P> event);
}
