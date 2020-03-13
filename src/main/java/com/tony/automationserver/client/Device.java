package com.tony.automationserver.client;

import com.tony.sqlhelper.annotation.Property;
import com.tony.sqlhelper.annotation.Table;

@Table("device")
public class Device extends Client {

    @Property(name = "device_key")
    public String deviceKey;

    @Override
    public String getKey() {
        return deviceKey;
    }

    @Override
    public String toString() {
        return "[Device] " + deviceKey;
    }

}