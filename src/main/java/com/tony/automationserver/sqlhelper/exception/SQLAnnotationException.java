
package com.tony.automationserver.sqlhelper.exception;

public abstract class SQLAnnotationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SQLAnnotationException(String message) {
        super(message);
    }

}