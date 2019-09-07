package com.tony.automationserver.client;

import java.util.HashMap;
import java.util.LinkedList;

import com.tony.automationserver.sqlhelper.SQLObject;
import com.tony.automationserver.sqlhelper.SQLHelper.SQLTypes;
import com.tony.automationserver.sqlhelper.annotation.OneToMany;
import com.tony.automationserver.sqlhelper.annotation.PrimaryKey;
import com.tony.automationserver.sqlhelper.annotation.Property;
import com.tony.automationserver.sqlhelper.annotation.Table;

@Table(name = "account")
public class Account extends SQLObject {
    
    @PrimaryKey
    @Property(name = "id", type = SQLTypes.Long)
    public Long id;

    @Property(name = "email")
    public String email;

    @Property(name = "token")
    public String token;

    @Property(name = "password")
    public String passwordHash;

    @OneToMany(targetEntity = Device.class, mappedBy = "account_id")
    public LinkedList<Client> devices;

    @OneToMany(targetEntity = User.class, mappedBy = "account_id")
    public LinkedList<Client> users;

    public Account(HashMap<String, Object> map) throws Exception {
        super(map);
    }
}