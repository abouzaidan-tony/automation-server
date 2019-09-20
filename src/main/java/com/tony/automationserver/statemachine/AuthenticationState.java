package com.tony.automationserver.statemachine;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.authenticator.Authenticator;
import com.tony.automationserver.authenticator.DeviceAuthenticator;
import com.tony.automationserver.authenticator.UserAuthenticator;
import com.tony.automationserver.client.Client;
import com.tony.automationserver.command.DeviceMessageAnalyzer;
import com.tony.automationserver.command.UserMessageAnalyzer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticationState extends State {

    private static Logger logger = LogManager.getLogger(AuthenticationState.class.getName());

    public AuthenticationState(ClientSession session) {
        super(session);
    }

    @Override
    public State Process() {
        byte[] data = getData();
        Authenticator<Client> auth = data[0] == 0x55 ? new UserAuthenticator() : new DeviceAuthenticator();
        Client c = auth.Authenticate(data);

        if(c == null)
            session.writeByte((byte) 0x00);
        else
            session.writeByte((byte) 0x01);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}

        
        if(c == null){
            logger.warn(() -> "Authentication Failed");
            return new FinalState(session);
        }

        logger.debug(() -> "Authentication success for " + c);
        
        session.setClient(c);

        if(data[0] == 0x55){
            ClientSession.getUserSessions().put(c.id, session);
            return new CommandState(session, new UserMessageAnalyzer());
        }

        ClientSession.getDevicesSessions().put(c.id, session);
        return new CommandState(session, new DeviceMessageAnalyzer());
    }

    @Override
    public boolean instantExecution() {
        return false;
    }
}