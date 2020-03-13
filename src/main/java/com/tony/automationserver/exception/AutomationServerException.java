package com.tony.automationserver.exception;

public abstract class AutomationServerException extends Exception {
	private static final long serialVersionUID = -374575559059406277L;
	private String errorCode;
    
    public AutomationServerException(String errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}