package com.tony.automationserver.client;

import com.tony.automationserver.sqlhelper.annotation.ManyToOne;
import com.tony.automationserver.sqlhelper.annotation.PrimaryKey;
import com.tony.automationserver.sqlhelper.annotation.Property;

import java.util.HashMap;

import com.tony.automationserver.sqlhelper.SQLObject;
import com.tony.automationserver.sqlhelper.SQLHelper.SQLTypes;

public abstract class Client extends SQLObject {

    @PrimaryKey
    @Property(name="id", type=SQLTypes.Long)
    public Long id;

    @Property(name = "connected", type = SQLTypes.Boolean)
    public boolean connected;

    @ManyToOne(targetEntity = Account.class, inverserdBy = "account_id")
    public Account account;

    public Client() {}

    public Client(HashMap<String, Object> map) throws Exception {
        super(map);
    }

    public abstract String  getKey();
}