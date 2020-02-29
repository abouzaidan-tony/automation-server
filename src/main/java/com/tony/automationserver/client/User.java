package com.tony.automationserver.client;

import com.tony.sqlhelper.annotation.Property;
import com.tony.sqlhelper.annotation.Table;

@Table(name = "user")
public class User extends Client {

    @Property(name = "user_key")
    public String userKey;

    @Override
    public String getKey() {
        return userKey;
    }

    @Override
    public String toString() {
        return "[USER] " + userKey;
    }
}