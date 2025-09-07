package io.scriptor.eswin.impl.db;

import org.jetbrains.annotations.NotNull;

public final class Column {

    private final Table table;
    private final String name;
    private final int typeId;
    private final String typeName;
    private final int columnSize;
    private final int decimalDigits;
    private final int numPrecisionRadix;
    private final int nullable;
    private final String remarks;
    private final String columnDef;
    private final int sqlDataType;
    private final int sqlDatetimeSub;
    private final int charOctetLength;
    private final int ordinalPosition;
    private final String isNullable;
    private final String scopeCatalog;
    private final String scopeSchema;
    private final String scopeTable;
    private final short sourceDataType;
    private final String isAutoIncrement;
    private final String isGeneratedColumn;

    public Column(
            final @NotNull Table table,
            final @NotNull String name
    ) {
        this.table = table;
        this.name = name;
        this.typeId = 0;
        this.typeName = "";
        this.columnSize = 0;
        this.decimalDigits = 0;
        this.numPrecisionRadix = 0;
        this.nullable = 0;
        this.remarks = "";
        this.columnDef = "";
        this.sqlDataType = 0;
        this.sqlDatetimeSub = 0;
        this.charOctetLength = 0;
        this.ordinalPosition = 0;
        this.isNullable = "";
        this.scopeCatalog = "";
        this.scopeSchema = "";
        this.scopeTable = "";
        this.sourceDataType = 0;
        this.isAutoIncrement = "";
        this.isGeneratedColumn = "";
    }

    public Column(
            final @NotNull Table table,
            final @NotNull String name,
            final int typeId,
            final @NotNull String typeName,
            final int columnSize,
            final int decimalDigits,
            final int numPrecisionRadix,
            final int nullable,
            final String remarks,
            final String columnDef,
            final int sqlDataType,
            final int sqlDatetimeSub,
            final int charOctetLength,
            final int ordinalPosition,
            final String isNullable,
            final String scopeCatalog,
            final String scopeSchema,
            final String scopeTable,
            final short sourceDataType,
            final String isAutoIncrement,
            final String isGeneratedColumn
    ) {
        this.table = table;
        this.name = name;
        this.typeId = typeId;
        this.typeName = typeName;
        this.columnSize = columnSize;
        this.decimalDigits = decimalDigits;
        this.numPrecisionRadix = numPrecisionRadix;
        this.nullable = nullable;
        this.remarks = remarks;
        this.columnDef = columnDef;
        this.sqlDataType = sqlDataType;
        this.sqlDatetimeSub = sqlDatetimeSub;
        this.charOctetLength = charOctetLength;
        this.ordinalPosition = ordinalPosition;
        this.isNullable = isNullable;
        this.scopeCatalog = scopeCatalog;
        this.scopeSchema = scopeSchema;
        this.scopeTable = scopeTable;
        this.sourceDataType = sourceDataType;
        this.isAutoIncrement = isAutoIncrement;
        this.isGeneratedColumn = isGeneratedColumn;
    }

    public @NotNull Table table() {
        return table;
    }

    public @NotNull String name() {
        return name;
    }

    public int typeId() {
        return typeId;
    }

    public @NotNull String typeName() {
        return typeName;
    }

    public int columnSize() {
        return columnSize;
    }

    public int decimalDigits() {
        return decimalDigits;
    }

    public int numPrecisionRadix() {
        return numPrecisionRadix;
    }

    public int nullable() {
        return nullable;
    }

    public String remarks() {
        return remarks;
    }

    public String columnDef() {
        return columnDef;
    }

    public int sqlDataType() {
        return sqlDataType;
    }

    public int sqlDatetimeSub() {
        return sqlDatetimeSub;
    }

    public int charOctetLength() {
        return charOctetLength;
    }

    public int ordinalPosition() {
        return ordinalPosition;
    }

    public String isNullable() {
        return isNullable;
    }

    public String scopeCatalog() {
        return scopeCatalog;
    }

    public String scopeSchema() {
        return scopeSchema;
    }

    public String scopeTable() {
        return scopeTable;
    }

    public short sourceDataType() {
        return sourceDataType;
    }

    public String isAutoIncrement() {
        return isAutoIncrement;
    }

    public String isGeneratedColumn() {
        return isGeneratedColumn;
    }

    @Override
    public @NotNull String toString() {
        return "%s.%s".formatted(table, name);
    }
}
