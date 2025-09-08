package io.scriptor.eswin.impl.db;

import org.jetbrains.annotations.NotNull;

public final class Column {

    private final Table table;
    private final String name;
    private ColumnType type;
    private int columnSize;
    private int decimalDigits;
    private int numPrecisionRadix;
    private String remarks;
    private String columnDef;
    private int sqlDataType;
    private int sqlDatetimeSub;
    private int charOctetLength;
    private int ordinalPosition;
    private String scopeCatalog;
    private String scopeSchema;
    private String scopeTable;
    private short sourceDataType;
    private String nullable;
    private String autoIncrement;
    private String isGeneratedColumn;

    public Column(
            final @NotNull Table table,
            final @NotNull String name
    ) {
        this.table = table;
        this.name = name;
    }

    public @NotNull Table table() {
        return table;
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull ColumnType getType() {
        return type;
    }

    public @NotNull Column setType(final @NotNull ColumnType type) {
        this.type = type;
        return this;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public @NotNull Column setColumnSize(final int columnSize) {
        this.columnSize = columnSize;
        return this;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public @NotNull Column setDecimalDigits(final int decimalDigits) {
        this.decimalDigits = decimalDigits;
        return this;
    }

    public int numPrecisionRadix() {
        return numPrecisionRadix;
    }

    public @NotNull Column setNumPrecisionRadix(final int numPrecisionRadix) {
        this.numPrecisionRadix = numPrecisionRadix;
        return this;
    }

    public @NotNull String remarks() {
        return remarks;
    }

    public @NotNull Column setRemarks(final @NotNull String remarks) {
        this.remarks = remarks;
        return this;
    }

    public @NotNull String columnDef() {
        return columnDef;
    }

    public @NotNull Column setColumnDef(final @NotNull String columnDef) {
        this.columnDef = columnDef;
        return this;
    }

    public int sqlDataType() {
        return sqlDataType;
    }

    public @NotNull Column setSqlDataType(final int sqlDataType) {
        this.sqlDataType = sqlDataType;
        return this;
    }

    public int sqlDatetimeSub() {
        return sqlDatetimeSub;
    }

    public @NotNull Column setSqlDatetimeSub(final int sqlDatetimeSub) {
        this.sqlDatetimeSub = sqlDatetimeSub;
        return this;
    }

    public int charOctetLength() {
        return charOctetLength;
    }

    public @NotNull Column setCharOctetLength(final int charOctetLength) {
        this.charOctetLength = charOctetLength;
        return this;
    }

    public int ordinalPosition() {
        return ordinalPosition;
    }

    public @NotNull Column setOrdinalPosition(final int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
        return this;
    }

    public @NotNull String scopeCatalog() {
        return scopeCatalog;
    }

    public @NotNull Column setScopeCatalog(final @NotNull String scopeCatalog) {
        this.scopeCatalog = scopeCatalog;
        return this;
    }

    public @NotNull String scopeSchema() {
        return scopeSchema;
    }

    public @NotNull Column setScopeSchema(final @NotNull String scopeSchema) {
        this.scopeSchema = scopeSchema;
        return this;
    }

    public @NotNull String scopeTable() {
        return scopeTable;
    }

    public @NotNull Column setScopeTable(final @NotNull String scopeTable) {
        this.scopeTable = scopeTable;
        return this;
    }

    public short getSourceDataType() {
        return sourceDataType;
    }

    public @NotNull Column setSourceDataType(final short sourceDataType) {
        this.sourceDataType = sourceDataType;
        return this;
    }

    public @NotNull String isNullable() {
        return nullable;
    }

    public @NotNull Column setNullable(final @NotNull String isNullable) {
        this.nullable = isNullable;
        return this;
    }

    public @NotNull String isAutoIncrement() {
        return autoIncrement;
    }

    public @NotNull Column setAutoIncrement(final @NotNull String isAutoIncrement) {
        this.autoIncrement = isAutoIncrement;
        return this;
    }

    public @NotNull String isGeneratedColumn() {
        return isGeneratedColumn;
    }

    public @NotNull Column setGeneratedColumn(final @NotNull String isGeneratedColumn) {
        this.isGeneratedColumn = isGeneratedColumn;
        return this;
    }

    @Override
    public @NotNull String toString() {
        return "%s.%s".formatted(table, name);
    }
}
