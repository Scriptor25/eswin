package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.context.Context;
import org.jetbrains.annotations.NotNull;

public interface RadioGroupContext extends Context {

    void addRadioButton(final @NotNull RadioButtonComponent radioButton);
}
