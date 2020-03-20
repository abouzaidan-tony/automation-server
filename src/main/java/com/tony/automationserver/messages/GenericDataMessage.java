package com.tony.automationserver.messages;

import java.util.Arrays;

import com.tony.automationserver.client.Client;

public class GenericDataMessage extends Message {

    private byte[] data;

    GenericDataMessage(MessageType mType, Client client, byte[] data) {
        super(mType, client);
        this.data = data;
        setOrigin(client.getKey());
    }

    GenericDataMessage(byte[] buffer, Client client) {
        super(buffer, client);
        data = Arrays.copyOfRange(buffer, 6, buffer.length);
    }

    @Override
    protected String resoleOrigin() {
        return new String(buffer, 1, 5);
    }

    @Override
    protected void init() {
        int totalLength = 6 + data.length;
        byte[] buffer = new byte[totalLength];
        buffer[0] = getMessageType().byteVal();
        byte[] idBuffer = getOrigin().getBytes();
        for (int i = 0; i < 5; i++)
            buffer[i + 1] = i < idBuffer.length ? idBuffer[i] : 0;
        for (int i = 0; i < data.length; i++)
            buffer[i + 6] = data[i];
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}