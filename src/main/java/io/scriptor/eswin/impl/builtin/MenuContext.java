package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.context.Context;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public interface MenuContext extends Context {

    void addMenu(final @NotNull JMenu menu);

    void addMenuItem(final @NotNull JMenuItem item);
}
