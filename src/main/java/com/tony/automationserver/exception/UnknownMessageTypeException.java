package com.tony.automationserver.exception;

public class UnknownMessageTypeException extends AutomationServerException {

    private static final long serialVersionUID = 7675651227815760923L;

    public UnknownMessageTypeException() {
        super("V001","Unknown Message Type");
    }
}