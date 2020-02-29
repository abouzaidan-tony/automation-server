package com.tony.automationserver.client;

import java.util.LinkedList;

import com.tony.sqlhelper.annotation.ManyToMany;
import com.tony.sqlhelper.annotation.OneToMany;
import com.tony.sqlhelper.annotation.PrimaryKey;
import com.tony.sqlhelper.annotation.Property;
import com.tony.sqlhelper.annotation.Table;
import com.tony.sqlhelper.helper.SQLHelper.SQLTypes;

@Table(name = "account")
public class Account {

    @PrimaryKey
    @Property(name = "id", type = SQLTypes.Long)
    private Long id;

    @Property(name = "email")
    private String email;

    @Property(name = "token")
    private String token;

    @Property(name = "password")
    private String passwordHash;

    @OneToMany(targetEntity = Device.class, mappedBy = "account_id")
    private LinkedList<Client> devices;

    @OneToMany(targetEntity = User.class, mappedBy = "account_id")
    private LinkedList<Client> users;

    @ManyToMany(targetEntity = Application.class, mappedBy = "account_id", inversedBy = "app_id", joinTable = "subscriptions")
    private LinkedList<Application> subscriptions;

    @Override
    public String toString() {
        return "[ACCOUNT] " + email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LinkedList<Client> getDevices() {
        return devices;
    }

    public void setDevices(LinkedList<Client> devices) {
        this.devices = devices;
    }

    public LinkedList<Client> getUsers() {
        return users;
    }

    public void setUsers(LinkedList<Client> users) {
        this.users = users;
    }

    public LinkedList<Application> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(LinkedList<Application> subscriptions) {
        this.subscriptions = subscriptions;
    }
}