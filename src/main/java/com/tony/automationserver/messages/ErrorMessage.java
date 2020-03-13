package com.tony.automationserver.messages;

import com.tony.automationserver.client.Client;
import com.tony.automationserver.exception.AutomationServerException;

public class ErrorMessage extends Message {

    private final String errorCode;
    private final String message;

    public ErrorMessage(String errorCode, String message, Client client) {
        super(MessageType.ERROR, client);
        this.errorCode = errorCode;
        this.message = message;
        init();
    }

    public ErrorMessage(AutomationServerException ex, Client client) {
        super(MessageType.ERROR, client);
        this.errorCode = ex.getErrorCode();
        this.message = ex.getMessage();
        init();
    }

    public ErrorMessage(byte[] buffer, Client client) {
        super(buffer, client);
        this.errorCode = new String(buffer, 6, 10);
        this.message = new String(buffer, 10, buffer.length - 10);
    }

    @Override
    protected String resoleOrigin() {
        return new String(buffer, 1, 5);
    }

    @Override
    protected void init() {
        int length = 10 + message.length();
        buffer = new byte[length];
        buffer[0] = getMessageType().byteVal();
        for (int i = 0; i < 5; i++)
            buffer[i + 1] = (byte) getOrigin().charAt(i);
        for (int i = 0; i < 4; i++)
            buffer[i + 6] = (byte) errorCode.charAt(i);
        for (int i = 0; i < message.length(); i++)
            buffer[i + 10] = (byte) message.charAt(i);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}