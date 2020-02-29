package com.tony.automationserver.client;

import com.tony.sqlhelper.helper.SQLHelper.SQLTypes;
import com.tony.sqlhelper.annotation.ManyToOne;
import com.tony.sqlhelper.annotation.PrimaryKey;
import com.tony.sqlhelper.annotation.Property;

public abstract class Client {

    @PrimaryKey
    @Property(name="id", type=SQLTypes.Long)
    private Long id;

    @Property(name = "connected", type = SQLTypes.Boolean)
    private boolean connected;

    @ManyToOne(targetEntity = Account.class, inverserdBy = "account_id")
    private Account account;

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