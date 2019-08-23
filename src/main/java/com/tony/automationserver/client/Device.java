package com.tony.automationserver.client;

import java.util.HashMap;

import com.tony.automationserver.sqlhelper.annotation.ManyToOne;
import com.tony.automationserver.sqlhelper.annotation.Property;
import com.tony.automationserver.sqlhelper.annotation.Table;

@Table(name = "device")
public class Device extends Client {

    @Property(name = "device_key")
    public String deviceKey;

    @ManyToOne(targetEntity = User.class, inverserdBy = "user_id")
    public User userClient;

    public Device(HashMap<String, Object> map) throws Exception {
        super(map);
    }

}