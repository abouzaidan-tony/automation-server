package com.tony.automationserver.exception;

public class DeviceNotConnectedException extends AutomationServerException {
    private static final long serialVersionUID = 2L;
    public DeviceNotConnectedException(){
        super("D002", "Device is not connected");
    }
}