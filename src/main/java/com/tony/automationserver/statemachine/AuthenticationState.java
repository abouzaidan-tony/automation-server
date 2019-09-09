package com.tony.automationserver.statemachine;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.authenticator.Authenticator;
import com.tony.automationserver.authenticator.DeviceAuthenticator;
import com.tony.automationserver.authenticator.UserAuthenticator;
import com.tony.automationserver.client.Client;
import com.tony.automationserver.command.DeviceMessageAnalyzer;
import com.tony.automationserver.command.UserMessageAnalyzer;

public class AuthenticationState extends State {

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

        
        if(c == null)
            return new FinalState(session);
        
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