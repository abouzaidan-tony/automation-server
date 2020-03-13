package com.tony.automationserver.messages;

import com.tony.automationserver.client.Client;

public class DataMessage extends GenericDataMessage {

    DataMessage(Client client, byte[] data) {
        super(MessageType.DATA,client, data);
    }
}