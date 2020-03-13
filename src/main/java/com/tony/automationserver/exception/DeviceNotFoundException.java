package com.tony.automationserver.exception;

public class DeviceNotFoundException extends AutomationServerException {
    private static final long serialVersionUID = 1L;

    public DeviceNotFoundException() {
        super("D001", "Device is not found");
    }
}