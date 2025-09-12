package io.scriptor.eswin.impl.model;

import org.jetbrains.annotations.NotNull;

public record ServerRef(
        @NotNull String label,
        @NotNull String url,
        @NotNull String username,
        @NotNull String password
) {
}
