package com.tony.automationserver.command;

import java.io.IOException;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.Session;
import com.tony.automationserver.client.Device;
import com.tony.automationserver.client.User;
import com.tony.automationserver.exception.DeviceNotConnectedException;
import com.tony.automationserver.exception.DeviceNotFoundException;
import com.tony.automationserver.messages.Message;
import com.tony.automationserver.messages.MessageBuilder;
import com.tony.automationserver.messages.Message.MessageType;

public class DeviceMessageAnalyzer implements MessageAnalyzer {

    @Override
    public void Process(Message message) {
		Device device = (Device) message.getClient();
        User c = device.userClient;
        if(!c.id.equals(message.getOrigin()))
            throw new DeviceNotFoundException();

        Session session = ClientSession.getUserSessions().get(c.id);

        if (session == null)
            throw new DeviceNotConnectedException();

        message.setOrigin(device.id);
        try {
            session.sendMessage(message.toByteArray());
        } catch (IOException ex) {
            Message m = new MessageBuilder().setKeepAlive(true).setOrigin(device)
                    .setMessage("Could not forward message").setMessageType(MessageType.ERROR)
                    .build();
            try {
                ClientSession.getDevicesSessions().get(device.id).sendMessage(m.toByteArray());
            } catch (Exception exx) {
            }
        }
	}
}