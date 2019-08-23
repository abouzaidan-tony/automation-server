package com.tony.automationserver.sqlhelper.filter;

public class LessFilterTuple extends FilterTuple {

    public LessFilterTuple(String columnName, Object value){
        super(columnName, value);
    }
    
    @Override
    public String toString()
    {
		return columnName + " < " + " ? ";
    }
}