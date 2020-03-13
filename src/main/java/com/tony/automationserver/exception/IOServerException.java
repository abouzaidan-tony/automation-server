package com.tony.automationserver.exception;

import java.io.IOException;

public class IOServerException extends AutomationServerException {

    private static final long serialVersionUID = 6402255423219874065L;

    public IOServerException(IOException ex) {
        super("IO00",ex.getMessage());
    }
}