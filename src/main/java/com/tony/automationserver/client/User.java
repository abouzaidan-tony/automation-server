package com.tony.automationserver.client;

import java.util.HashMap;
import java.util.LinkedList;

import com.tony.automationserver.sqlhelper.annotation.OneToMany;
import com.tony.automationserver.sqlhelper.annotation.Property;
import com.tony.automationserver.sqlhelper.annotation.Table;

@Table(name = "user")
public class User extends Client {

    @Property(name = "email")
    public String email;

    @Property(name = "token")
    public String token;

    @Property(name = "password")
    public String passwordHash;

    @OneToMany(targetEntity = Device.class, mappedBy = "user_id")
    public LinkedList<Device> devices;

    public User(HashMap<String, Object> map) throws Exception {
        super(map);
    }
}