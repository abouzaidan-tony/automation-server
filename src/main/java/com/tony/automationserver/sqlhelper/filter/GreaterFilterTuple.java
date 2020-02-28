package com.tony.automationserver.sqlhelper.filter;

public class GreaterFilterTuple extends FilterTuple {

    public GreaterFilterTuple(String columnName, Object value) {
        super(columnName, value);
    }

    @Override
    public String toString() {
        return columnName + " > " + " ? ";
    }
}