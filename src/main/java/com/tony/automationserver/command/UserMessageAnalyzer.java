package com.tony.automationserver.command;

import java.io.IOException;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.Session;
import com.tony.automationserver.client.Client;
import com.tony.automationserver.client.Device;
import com.tony.automationserver.client.User;
import com.tony.automationserver.exception.DeviceNotConnectedException;
import com.tony.automationserver.exception.DeviceNotFoundException;
import com.tony.automationserver.messages.Message;
import com.tony.automationserver.messages.MessageBuilder;
import com.tony.automationserver.messages.Message.MessageType;

public class UserMessageAnalyzer implements MessageAnalyzer {

    @Override
    public void Process(Message message) {
        User client = (User) message.getClient();

        boolean isBroadcast = false;
        if(message.getMessageType() == MessageType.BROADCAST) {
            isBroadcast = true;
        }

        Device c = null;
        for (Device var : client.devices) {
            if(var.id.equals(message.getOrigin()))
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
    
    private void sendMessage(Message message ,Client c, Client origin){

        Session session = ClientSession.getDevicesSessions().get(c.id);

        if (session == null)
            throw new DeviceNotConnectedException();

        message.setOrigin(origin.id);
        try {
            session.sendMessage(message.toByteArray());
        } catch (IOException ex) {
            Message m = new MessageBuilder().setKeepAlive(true).setOrigin(origin)
                    .setMessage("Could not forward message").setMessageType(MessageType.ERROR).build();
            try {
                ClientSession.getUserSessions().get(origin.id).sendMessage(m.toByteArray());
            } catch (Exception exx) {
            }
        }

    }
}