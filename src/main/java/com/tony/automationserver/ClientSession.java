package com.tony.automationserver;

import java.net.Socket;
import java.util.HashMap;

import com.tony.automationserver.client.Client;
import com.tony.automationserver.client.User;
import com.tony.automationserver.sqlhelper.EntityManager;
import com.tony.automationserver.statemachine.StateMachine;
import com.tony.automationserver.streams.BytesStreamManager;

public class ClientSession extends Session {

    private static HashMap<Long, ClientSession> userSessions;
    private static HashMap<Long, ClientSession> deviceSessions;

    private Client client;

    public static HashMap<Long, ClientSession> getUserSessions() {
        return userSessions;
    }

    public static HashMap<Long, ClientSession> getDevicesSessions() {
        return deviceSessions;
    }

    static{
        userSessions = new HashMap<>();
        deviceSessions = new HashMap<>();
    }

    private StateMachine stateMachine;

    public ClientSession(Socket socket)
    {
        super(socket, new BytesStreamManager());
        stateMachine = new StateMachine(this);
    }

    public void OnMessageReady(byte[] message) {
        stateMachine.Process(message);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void OnSessionClosed() {
        if(client == null)
            return;
        client.connected = false;
        EntityManager.GetInstance().Update(client);
        EntityManager.GetInstance().flush();
        if(client instanceof User)
            userSessions.remove(client.id);
        else
            deviceSessions.remove(client.id);
        client = null;
    }
}