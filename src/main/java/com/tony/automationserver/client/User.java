package com.tony.automationserver.client;

import java.util.HashMap;

import com.tony.automationserver.sqlhelper.annotation.Property;
import com.tony.automationserver.sqlhelper.annotation.Table;

@Table(name = "user")
public class User extends Client {

    @Property(name = "user_key")
    public String userKey;

    public User(HashMap<String, Object> map) throws Exception {
        super(map);
    }

    @Override
    public String getKey() {
        return userKey;
    }

    @Override
    public String toString() {
        return "[USER] " + userKey;
    }
}