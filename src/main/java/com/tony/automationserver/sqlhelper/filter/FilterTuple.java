package com.tony.automationserver.sqlhelper.filter;

public class FilterTuple{
	
    protected String columnName;
    protected Object value;

    public FilterTuple(String columnName, Object value){
        this.columnName = columnName;
        this.value = value;
    }
    
    @Override
    public String toString()
    {
		return columnName + " = " + " ? ";
    }

    public Object Value(){
        return value;
    }
}