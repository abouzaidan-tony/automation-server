package com.tony.automationserver.command;

import java.util.List;

import com.tony.automationserver.Session;
import com.tony.automationserver.client.Client;
import com.tony.automationserver.exception.AutomationServerException;
import com.tony.automationserver.exception.DeviceNotConnectedException;
import com.tony.automationserver.exception.DeviceNotFoundException;
import com.tony.automationserver.messages.ErrorMessageBuilder;
import com.tony.automationserver.messages.Message;
import com.tony.automationserver.messages.Message.MessageType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractMessageAnalyzer implements MessageAnalyzer {

    private static Logger log = LogManager.getLogger(AbstractMessageAnalyzer.class);

    public abstract List<Client> getCandidates(Client client);

    public abstract Session getSessionById(long id);

    public abstract Session getSelfSessionById(long id);

    @Override
    public void Process(Message message) throws AutomationServerException {
        Client origin = (Client) message.getClient();

        Session session = getSelfSessionById(origin.getId());

        boolean isBroadcast = message.getMessageType() == MessageType.BROADCAST;
        boolean isEcho = message.getMessageType() == MessageType.ECHO;

        Client c = null;

        if (isEcho) {
            c = origin;
        } else {
            for (Client var : getCandidates(origin)) {
                if (var.getKey().equals(message.getOrigin()) || isBroadcast) {
                    c = var;
                    if (!isBroadcast)
                        break;
                    processSingleMessage(message, c, origin, session);
                }
            }
        }

        if (!isBroadcast)
            processSingleMessage(message, c, origin, session);
    }

    private void processSingleMessage(Message message, Client c, Client origin, Session session)
            throws AutomationServerException {
        try {

            sendMessage(message, c, origin);

        } catch (AutomationServerException ex) {
            log.info(origin + " -> " + message.getOrigin() + " [" + ex.getMessage() + "]");
            message = new ErrorMessageBuilder().setException(ex).setOrigin(origin).build();
            session.sendMessage(message.toByteArray());
        }
    }

    protected final void sendMessage(Message message, Client c, Client origin) throws AutomationServerException {

        if (c == null)
            throw new DeviceNotFoundException();

        log.debug("Sending message from" + origin + " to " + c);
        Session session = getSessionById(c.getId());
        message.setOrigin(c.getKey());

        if (session == null)
            throw new DeviceNotConnectedException();

        message.setOrigin(origin.getKey());
        session.sendMessage(message.toByteArray());
    }
}