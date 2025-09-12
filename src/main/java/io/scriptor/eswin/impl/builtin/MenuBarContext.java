package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.context.Context;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public interface MenuBarContext extends Context {

    void addMenu(final @NotNull JMenu menu);
}
