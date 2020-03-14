package com.tony.automationserver.command;

import java.util.List;

import com.tony.automationserver.Session;
import com.tony.automationserver.client.Client;
import com.tony.automationserver.exception.AutomationServerException;
import com.tony.automationserver.exception.DeviceNotConnectedException;
import com.tony.automationserver.exception.DeviceNotFoundException;
import com.tony.automationserver.messages.Message;
import com.tony.automationserver.messages.Message.MessageType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractMessageAnalyzer implements MessageAnalyzer {

    private static Logger log = LogManager.getLogger(AbstractMessageAnalyzer.class);

    public abstract List<Client> getCandidates(Client client);

    public abstract Session getSessionById(long id);

    @Override
    public void Process(Message message) throws AutomationServerException {
        Client client = (Client) message.getClient();

        boolean isBroadcast = false;
        if (message.getMessageType() == MessageType.BROADCAST) {
            isBroadcast = true;
            log.debug("Broadcast message from " + client);
        }

        Client c = null;
        for (Client var : getCandidates(client)) {
            if (var.getKey().equals(message.getOrigin())) {
                c = var;
                if (!isBroadcast)
                    break;
                sendMessage(message, c, client);
            }
        }

        if (c == null)
            throw new DeviceNotFoundException();

        if (!isBroadcast)
            sendMessage(message, c, client);
    }

    protected final void sendMessage(Message message, Client c, Client origin) throws AutomationServerException {
        log.debug("Sending message from" + origin + " to " + c);
        Session session = getSessionById(c.getId());

        if (session == null)
            throw new DeviceNotConnectedException();

        message.setOrigin(origin.getKey());
            session.sendMessage(message.toByteArray());
            // logger.error(ex.getMessage(), ex);
    // Message m;
    //     m = new ErrorMessageBuilder().setException(ex).setOrigin(origin).build();

    // if (origin instanceof User)
    //     ClientSession.getUserSessions().get(origin.getId()).sendMessage(m.toByteArray());
    // else
    //     ClientSession.getDevicesSessions().get(origin.getId()).sendMessage(m.toByteArray());
    }
}