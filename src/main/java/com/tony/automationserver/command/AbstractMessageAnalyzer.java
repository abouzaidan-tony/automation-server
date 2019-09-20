package com.tony.automationserver.command;

import java.io.IOException;
import java.util.List;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.Session;
import com.tony.automationserver.client.Client;
import com.tony.automationserver.client.User;
import com.tony.automationserver.exception.DeviceNotConnectedException;
import com.tony.automationserver.exception.DeviceNotFoundException;
import com.tony.automationserver.messages.Message;
import com.tony.automationserver.messages.MessageBuilder;
import com.tony.automationserver.messages.Message.MessageType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractMessageAnalyzer implements MessageAnalyzer {

    private static Logger logger = LogManager.getLogger(AbstractMessageAnalyzer.class.getName());

    public abstract List<Client> getCandidates(Client client);
    public abstract Session getSessionById(long id);

    @Override
    public void Process(Message message) {
        Client client = (Client) message.getClient();

        boolean isBroadcast = false;
        if(message.getMessageType() == MessageType.BROADCAST) {
            isBroadcast = true;
            logger.debug(() -> "Broadcast message from " + client);
        }

        Client c = null;
        for (Client var : getCandidates(client)) {
            if(var.getKey().equals(message.getOrigin()))
            {
                c = var;
                if(!isBroadcast)
                    break;
                sendMessage(message, c, client);
            }
        }

        if(c == null)
            throw new DeviceNotFoundException();

        if(!isBroadcast)
            sendMessage(message, c, client);
    }
    
    protected final void sendMessage(Message message, Client c, Client origin){
        logger.debug(() -> "Sending message from" + origin + " to " + c);
        Session session = getSessionById(c.id);

        if (session == null)
            throw new DeviceNotConnectedException();

        message.setOrigin(origin.getKey());
        try {
            session.sendMessage(message.toByteArray());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            Message m = new MessageBuilder().setKeepAlive(true).setOrigin(origin)
                    .setMessage("Could not forward message").setMessageType(MessageType.ERROR).build();
            try {
                if(origin instanceof User)
                    ClientSession.getUserSessions().get(origin.id).sendMessage(m.toByteArray());
                else
                    ClientSession.getDevicesSessions().get(origin.id).sendMessage(m.toByteArray());
            } catch (Exception exx) {
                logger.error(exx.getMessage(), exx);
            }
        }

    }
}