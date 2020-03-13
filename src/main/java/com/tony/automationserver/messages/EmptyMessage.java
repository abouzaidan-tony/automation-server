package com.tony.automationserver.messages;

import com.tony.automationserver.client.Client;

public class EmptyMessage extends Message {

    EmptyMessage(Client client) {
        super(MessageType.EMPTY, client);
    }

    @Override
    public byte[] toByteArray() {
        return null;
    }

    @Override
    protected void init() {

    }

    @Override
    protected String resoleOrigin() {
        return null;
    }
}