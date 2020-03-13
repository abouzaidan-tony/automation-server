package com.tony.automationserver.messages;

import com.tony.automationserver.client.Client;

public class EchoMessage extends GenericDataMessage {

    EchoMessage(Client client, byte[] data) {
        super(MessageType.ECHO,client, data);
    }
}