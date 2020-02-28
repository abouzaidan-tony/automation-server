package com.tony.automationserver.exception;

public class DeviceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DeviceNotFoundException() {
        super("Device is not found");
    }
}