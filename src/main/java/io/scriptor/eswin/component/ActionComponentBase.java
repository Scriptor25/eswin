package io.scriptor.eswin.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static io.scriptor.eswin.util.EslHelper.getActionListener;

public abstract class ActionComponentBase<C extends ActionComponentBase<C, P>, P> extends ComponentBase {

    public ActionComponentBase(
            final @NotNull ContextProvider provider,
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(provider, parent, attributes, text);
    }

    @Override
    protected void apply(final @NotNull JComponent component) {
        super.apply(component);

        if (getAttributes().has("action"))
            addListener(getActionListener(getParent(), getAttributes().get("action")));
    }

    public abstract void addListener(final @NotNull ActionListener<C, P> listener);
}
