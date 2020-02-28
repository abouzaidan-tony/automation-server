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
    private Long id;

    @Property(name = "connected", type = SQLTypes.Boolean)
    private boolean connected;

    @ManyToOne(targetEntity = Account.class, inverserdBy = "account_id")
    private Account account;

    public Client() {}

    public Client(HashMap<String, Object> map) throws Exception {
        super(map);
    }

    public abstract String  getKey();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}