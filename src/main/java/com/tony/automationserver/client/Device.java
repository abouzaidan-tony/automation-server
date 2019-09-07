package com.tony.automationserver.client;

import java.util.HashMap;

import com.tony.automationserver.sqlhelper.annotation.Property;
import com.tony.automationserver.sqlhelper.annotation.Table;

@Table(name = "device")
public class Device extends Client {

    @Property(name = "device_key")
    public String deviceKey;

    public Device(HashMap<String, Object> map) throws Exception {
        super(map);
    }

    @Override
    public String getKey() {
        return deviceKey;
    }

}