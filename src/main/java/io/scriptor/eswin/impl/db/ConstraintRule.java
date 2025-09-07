package io.scriptor.eswin.impl.db;

import org.jetbrains.annotations.NotNull;

import java.sql.DatabaseMetaData;

public enum ConstraintRule {
    CASCADE,
    RESTRICT,
    SET_NULL,
    NO_ACTION,
    SET_DEFAULT;

    public static @NotNull ConstraintRule of(final short value) {
        return switch (value) {
            case DatabaseMetaData.importedKeyCascade -> CASCADE;
            case DatabaseMetaData.importedKeyRestrict -> RESTRICT;
            case DatabaseMetaData.importedKeySetNull -> SET_NULL;
            case DatabaseMetaData.importedKeyNoAction -> NO_ACTION;
            case DatabaseMetaData.importedKeySetDefault -> SET_DEFAULT;
            default -> throw new IllegalStateException();
        };
    }
}
