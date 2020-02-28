package com.tony.automationserver.messages;

import java.util.Arrays;

import com.tony.automationserver.client.Client;

public class Message {

    public enum MessageType {
        DATA, ERROR, ECHO, BROADCAST
    }

    private MessageType mType;
    private byte type;
    private byte keepAlive;
    private String origin;
    private byte[] data;
    private Client client;

    protected Message() {
    }

    Message(MessageType mType, String origin, byte[] data, byte keepAlive) {
        iniType(mType);
        this.mType = mType;
        this.origin = origin;
        this.data = data;
        this.keepAlive = keepAlive;
    }

    public Message(byte[] buffer, Client client) {
        if (buffer.length < 7)
            throw new IllegalArgumentException();
        this.type = buffer[0];
        this.keepAlive = buffer[1];
        this.origin = new String(buffer, 2, 5);
        iniMType(type);
        data = Arrays.copyOfRange(buffer, 7, buffer.length);
        this.client = client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private void iniType(MessageType mType) {
        switch (mType) {
            case DATA:
                this.type = 1;
                break;
            case ECHO:
                this.type = 2;
                break;
            case BROADCAST:
                this.type = 3;
                break;
            default:
                this.type = 4;
                break;
        }
    }

    private void iniMType(byte type) {
        switch (type) {
            case 1:
                this.mType = MessageType.DATA;
                break;
            case 2:
                this.mType = MessageType.ECHO;
                break;
            case 3:
                this.mType = MessageType.BROADCAST;
                break;
            default:
                this.mType = MessageType.ERROR;
                break;
        }
    }

    public Client getClient() {
        return client;
    }

    public byte getType() {
        return type;
    }

    public String getOrigin() {
        return origin;
    }

    public byte[] getData() {
        return data;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public byte[] toByteArray() {
        int totalLength = 7 + data.length;
        byte[] buffer = new byte[totalLength];
        buffer[0] = type;
        buffer[1] = keepAlive;
        byte[] idBuffer = origin.getBytes();
        for (int i = 0; i < 5; i++)
            buffer[i + 2] = i < idBuffer.length ? idBuffer[i] : 0;
        for (int i = 0; i < data.length; i++)
            buffer[i + 7] = data[i];
        return buffer;
    }

    public MessageType getMessageType() {
        return mType;
    }

    public boolean KeepAlive() {
        return keepAlive == 1;
    }
}