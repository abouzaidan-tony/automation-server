package com.tony.automationserver.messages;

import com.tony.automationserver.client.Client;

public abstract class Message {

    public enum MessageType {
        DATA(1), ERROR(2), ECHO(3), BROADCAST(4), EMPTY(5);

        private final byte value;

        private MessageType(int value){
            this.value = (byte) value;
        }

        public byte byteVal(){
            return value;
        }

        public static MessageType fromByte(byte value) {
            for(MessageType e : MessageType.class.getEnumConstants()){
                if(e.byteVal() == value){
                    return e;
                }
            } 
            throw new IllegalArgumentException();           
        }

    }

    public final static String originServer = "00000";

    private final MessageType mType;
    protected byte[] buffer;
    private final Client client;
    private String origin;

    Message(MessageType mType, Client client) {
        this.mType = mType;
        this.client = client;
        origin = originServer;
    }

    Message(byte[] buffer, Client client) {
        this.mType = MessageType.fromByte(buffer[0]);
        this.buffer = buffer;
        this.client = client;
        this.origin = resoleOrigin();
    }

    protected abstract void init();

    protected abstract String resoleOrigin();

    public byte[] toByteArray(){
        return buffer;
    }

    public MessageType getMessageType() {
        return mType;
    }

    public Client getClient() {
        return client;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
        init();
    }
}