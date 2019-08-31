package com.tony.automationserver.client;

import com.tony.automationserver.sqlhelper.annotation.PrimaryKey;
import com.tony.automationserver.sqlhelper.annotation.Property;

import java.util.HashMap;

import com.tony.automationserver.sqlhelper.SQLObject;
import com.tony.automationserver.sqlhelper.SQLHelper.SQLTypes;

public abstract class Client extends SQLObject {

    @PrimaryKey
    @Property(name="id", type=SQLTypes.Long)
    public String id;

    public Client() {}

    public Client(HashMap<String, Object> map) throws Exception {
        super(map);
    }
}