package io.scriptor.eswin.component.action;

import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static io.scriptor.eswin.util.ComponentEslUtil.getActionListener;

public abstract class ActionComponentBase<C extends ActionComponentBase<C, P>, P> extends ComponentBase {

    public ActionComponentBase(final @NotNull ComponentInfo info) {
        super(info);
    }

    @Override
    protected void apply(final @NotNull JComponent component) {
        super.apply(component);

        if (getAttributes().has("action"))
            addListener(getActionListener(getParent(), getAttributes().get("action")));
    }

    public abstract void addListener(final @NotNull ActionListener<C, P> listener);
}
