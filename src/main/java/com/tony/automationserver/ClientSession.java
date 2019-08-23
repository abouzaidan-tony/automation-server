package com.tony.automationserver;

import java.net.Socket;
import java.util.HashMap;

import com.tony.automationserver.client.Client;
import com.tony.automationserver.statemachine.StateMachine;
import com.tony.automationserver.streams.BytesStreamManager;

public class ClientSession extends Session {

    private static HashMap<String, ClientSession> userSessions;
    private static HashMap<String, ClientSession> deviceSessions;

    private Client client;

    public static HashMap<String, ClientSession> getUserSessions() {
        return userSessions;
    }

    public static HashMap<String, ClientSession> getDevicesSessions() {
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
}