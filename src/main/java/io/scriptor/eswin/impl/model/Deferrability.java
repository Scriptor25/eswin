package io.scriptor.eswin.impl.model;

import org.jetbrains.annotations.NotNull;

import java.sql.DatabaseMetaData;

public enum Deferrability {
    INITIALLY_DEFERRED,
    INITIALLY_IMMEDIATE,
    NOT_DEFERRABLE;

    public static @NotNull Deferrability of(final short value) {
        return switch (value) {
            case DatabaseMetaData.importedKeyInitiallyDeferred -> INITIALLY_DEFERRED;
            case DatabaseMetaData.importedKeyInitiallyImmediate -> INITIALLY_IMMEDIATE;
            case DatabaseMetaData.importedKeyNotDeferrable -> NOT_DEFERRABLE;
            default -> throw new IllegalStateException();
        };
    }
}
