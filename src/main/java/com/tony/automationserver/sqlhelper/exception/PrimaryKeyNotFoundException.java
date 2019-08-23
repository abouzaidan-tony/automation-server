package com.tony.automationserver.sqlhelper.exception;

public class PrimaryKeyNotFoundException extends SQLAnnotationException {

    private static final long serialVersionUID = 1L;

    public PrimaryKeyNotFoundException(){
        super("Primary Key not set, Please use @PrimaryKey annotation");
    }
    
}