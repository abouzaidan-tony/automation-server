package com.tony.automationserver.messages;

import com.tony.automationserver.exception.AutomationServerException;

public class ErrorMessageBuilder extends MessageBuilder {

    private String errorCode;
    private String message;
    private AutomationServerException exception;

    public Message build() {
        if(exception != null)
            return new ErrorMessage(exception, getOrigin());
        return new ErrorMessage(errorCode, message, getOrigin());
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ErrorMessageBuilder setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ErrorMessageBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public AutomationServerException getException() {
        return exception;
    }

    public ErrorMessageBuilder setException(AutomationServerException exception) {
        this.exception = exception;
        return this;
    }
}