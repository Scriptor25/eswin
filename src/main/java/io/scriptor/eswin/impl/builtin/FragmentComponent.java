package io.scriptor.eswin.impl.builtin;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import org.jetbrains.annotations.NotNull;

@Component("fragment")
public class FragmentComponent extends ComponentBase {

    public FragmentComponent(final @NotNull ComponentInfo info) {
        super(info);
    }
}
