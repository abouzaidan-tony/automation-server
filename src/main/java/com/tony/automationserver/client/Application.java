package com.tony.automationserver.client;

import com.tony.sqlhelper.helper.SQLHelper.SQLTypes;
import com.tony.sqlhelper.annotation.PrimaryKey;
import com.tony.sqlhelper.annotation.Property;
import com.tony.sqlhelper.annotation.Table;

@Table("application")
public class Application {

    @PrimaryKey
    @Property(name="id", type=SQLTypes.Long)
    private Long id;

    @Property(name="app_token", type = SQLTypes.String)
    public String token;

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