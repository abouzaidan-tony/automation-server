package com.tony.automationserver.client;

import java.util.HashMap;

import com.tony.automationserver.sqlhelper.SQLObject;
import com.tony.automationserver.sqlhelper.SQLHelper.SQLTypes;
import com.tony.automationserver.sqlhelper.annotation.PrimaryKey;
import com.tony.automationserver.sqlhelper.annotation.Property;
import com.tony.automationserver.sqlhelper.annotation.Table;

@Table(name = "application")
public class Application extends SQLObject {

    @PrimaryKey
    @Property(name="id", type=SQLTypes.Long)
    private Long id;

    @Property(name="app_token", type = SQLTypes.String)
    public String token;

    public Application(HashMap<String, Object> map) throws Exception {
        super(map);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}