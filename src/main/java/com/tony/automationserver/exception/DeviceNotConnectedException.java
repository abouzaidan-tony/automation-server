package com.tony.automationserver.exception;

public class DeviceNotConnectedException extends RuntimeException {
    private static final long serialVersionUID = 2L;
    public DeviceNotConnectedException(){
        super("Device is not connected");
    }
}