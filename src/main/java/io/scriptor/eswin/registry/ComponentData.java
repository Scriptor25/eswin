package io.scriptor.eswin.registry;

import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.xml.XmlDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ComponentData(@NotNull Class<? extends ComponentBase> type, @Nullable XmlDocument layout) {
}
