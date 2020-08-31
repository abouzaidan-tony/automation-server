package com.tony.automationserver.statemachine;

import java.util.concurrent.Semaphore;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.authenticator.Authenticator;
import com.tony.automationserver.authenticator.DeviceAuthenticator;
import com.tony.automationserver.authenticator.UserAuthenticator;
import com.tony.automationserver.client.Client;
import com.tony.automationserver.command.DeviceMessageAnalyzer;
import com.tony.automationserver.command.MessageAnalyzer;
import com.tony.automationserver.command.UserMessageAnalyzer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticationState extends State {

    private static Logger logger = LogManager.getLogger(AuthenticationState.class);
    private static Semaphore authLock = new Semaphore(1);

    public AuthenticationState(ClientSession session) {
        super(session);
    }

    @Override
    public State Process() {
        byte[] data = getData();
        Authenticator<Client> auth = data[0] == 0x55 ? new UserAuthenticator()
                : data[0] == 'D' ? new DeviceAuthenticator() : null;

        if (auth == null)
            return new FinalState(session);

        try {
            authLock.acquire();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

        Client c = auth.Authenticate(data);

        if (c == null)
            return handleAuthFailed();
        return handleAuthSuccess(c);
    }

    @Override
    public boolean instantExecution() {
        return false;
    }

    private State handleAuthSuccess(Client c) {
        session.writeByte((byte) 0x01);

        logger.info("Authentication success for " + c);

        session.setClient(c);

        MessageAnalyzer analyzer = null;

        try {
            ClientSession.lock.acquire();
        } catch (InterruptedException e) {
        }

        if (data[0] == 0x55) {
            ClientSession.getUserSessions().put(c.getId(), session);
            analyzer = new UserMessageAnalyzer();
        } else {
            ClientSession.getDevicesSessions().put(c.getId(), session);
            analyzer = new DeviceMessageAnalyzer();
        }
        ClientSession.lock.release();
        authLock.release();
        return new CommandState(session, analyzer);
    }

    private State handleAuthFailed() {
        session.writeByte((byte) 0x00);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("Error", e);
        }
        logger.warn("Authentication Attempt Failed");
        authLock.release();
        return new FinalState(session);
    }
}