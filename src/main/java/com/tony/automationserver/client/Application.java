package com.tony.automationserver.client;

import com.tony.automationserver.sqlhelper.SQLObject;
import com.tony.automationserver.sqlhelper.SQLHelper.SQLTypes;
import com.tony.automationserver.sqlhelper.annotation.PrimaryKey;
import com.tony.automationserver.sqlhelper.annotation.Property;
import com.tony.automationserver.sqlhelper.annotation.Table;

@Table(name = "application")
public class Application extends SQLObject {

    @PrimaryKey
    @Property(name="id", type=SQLTypes.Long)
    public Long id;

    @Property(name="app_token", type = SQLTypes.String)
    public String token;
}