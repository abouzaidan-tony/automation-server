package com.tony.automationserver.sqlhelper.exception;

public class TableNameNotSetException extends SQLAnnotationException {

    private static final long serialVersionUID = 1L;

    public TableNameNotSetException(){
        super("Table Name not set, Please use @Table(name =\"\") annotation on your class");
    }
    
}