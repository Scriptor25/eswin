package io.scriptor.eswin.impl.db;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum ColumnType {
    BIT(-7),
    TINYINT(-6),
    SMALLINT(5),
    INTEGER(4),
    BIGINT(-5),
    FLOAT(6),
    REAL(7),
    DOUBLE(8),
    NUMERIC(2),
    DECIMAL(3),
    CHAR(1),
    VARCHAR(12),
    LONGVARCHAR(-1),
    DATE(91),
    TIME(92),
    TIMESTAMP(93),
    BINARY(-2),
    VARBINARY(-3),
    LONGVARBINARY(-4),
    NULL(0),
    OTHER(1111),
    JAVA_OBJECT(2000),
    DISTINCT(2001),
    STRUCT(2002),
    ARRAY(2003),
    BLOB(2004),
    CLOB(2005),
    REF(2006),
    DATALINK(70),
    BOOLEAN(16),
    ROWID(-8),
    NCHAR(-15),
    NVARCHAR(-9),
    LONGNVARCHAR(-16),
    NCLOB(2011),
    SQLXML(2009),
    REF_CURSOR(2012),
    TIME_WITH_TIMEZONE(2013),
    TIMESTAMP_WITH_TIMEZONE(2014);

    private final int typeId;

    ColumnType(final int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }

    public static @NotNull ColumnType from(final int typeId) {
        return Arrays.stream(values())
                     .filter(type -> type.getTypeId() == typeId)
                     .findAny()
                     .orElseThrow(() -> new NoSuchElementException("no column type with id '%d'".formatted(typeId)));
    }
}
