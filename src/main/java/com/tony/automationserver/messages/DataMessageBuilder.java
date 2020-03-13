package com.tony.automationserver.messages;

public class DataMessageBuilder extends MessageBuilder {

    private byte[] message;

    public DataMessageBuilder setMessage(byte[] message) {
        this.message = message;
        return this;
    }

    public DataMessageBuilder setMessage(String message) {
        this.message = message.getBytes();
        return this;
    }

    public Message build() {
        Message m = new DataMessage(getOrigin(), message);
        return m;
    }

}