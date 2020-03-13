package com.tony.automationserver.messages;

import com.tony.automationserver.client.Client;

public class BCMessage extends GenericDataMessage {

    BCMessage(Client client, byte[] data) {
        super(MessageType.BROADCAST, client, data);
    }
}