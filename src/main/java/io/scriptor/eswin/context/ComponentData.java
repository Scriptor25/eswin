package io.scriptor.eswin.context;

import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.xml.document.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ComponentData(@NotNull Class<? extends ComponentBase> type, @Nullable Document layout) {
}
