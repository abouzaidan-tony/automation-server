package com.tony.automationserver;

import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import com.tony.automationserver.client.Client;
import com.tony.automationserver.client.User;
import com.tony.automationserver.sqlhelper.EntityManager;
import com.tony.automationserver.statemachine.StateMachine;
import com.tony.automationserver.streams.BytesStreamManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientSession extends Session {

    private static Logger logger = LogManager.getLogger(ClientSession.class.getName());

    private static HashMap<Long, ClientSession> userSessions;
    private static HashMap<Long, ClientSession> deviceSessions;
    public static Semaphore lock = new Semaphore(1);

    private Client client;

    public static HashMap<Long, ClientSession> getUserSessions() {
        return userSessions;
    }

    public static HashMap<Long, ClientSession> getDevicesSessions() {
        return deviceSessions;
    }

    static {
        userSessions = new HashMap<>();
        deviceSessions = new HashMap<>();
    }

    private StateMachine stateMachine;

    public ClientSession(Socket socket) {
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
        if (client == null)
            return;
        logger.info(() -> "Removing client " + client);
        client.setConnected(false);
        EntityManager.GetInstance().Update(client);
        EntityManager.GetInstance().flush();
        try {
            lock.acquire();
        } catch (InterruptedException e) {}
        if(client instanceof User)
            userSessions.remove(client.getId());
        else
            deviceSessions.remove(client.getId());
        lock.release();
        client = null;
    }
}