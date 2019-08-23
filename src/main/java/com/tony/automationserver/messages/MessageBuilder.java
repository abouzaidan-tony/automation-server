package com.tony.automationserver.messages;

import com.tony.automationserver.client.Client;
import com.tony.automationserver.messages.Message.MessageType;

public class MessageBuilder {

    private byte[] message;
    private MessageType type;
    private String origin;
    private byte keepAlive;

    public MessageBuilder(){

    }

    public MessageBuilder setMessageType(MessageType type){
        this.type = type;
        return this;
    }

    public MessageBuilder setMessage(byte[] message){
        this.message = message;
        return this;
    }

    public MessageBuilder setMessage(String message){
        this.message = message.getBytes();
        return this;
    }

    public MessageBuilder setOrigin(Client origin){
        this.origin = origin.id;
        return this;
    }

    public MessageBuilder setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public MessageBuilder setKeepAlive(boolean keepAlive) {
        this.keepAlive = (byte)(keepAlive ? 1 : 0);
        return this;
    }

    public Message build() {
        Message m = new Message(type, origin, message, keepAlive);
        return m;
    }

}