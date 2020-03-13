package com.tony.automationserver.messages;

import com.tony.automationserver.exception.AutomationServerException;
import com.tony.automationserver.exception.UnknownMessageTypeException;
import com.tony.automationserver.messages.Message.MessageType;

public class BufferMessageBuilder  extends MessageBuilder {

    private byte[] buffer;

    public BufferMessageBuilder() {

    }


    public Message build() throws AutomationServerException {
        MessageType mType = MessageType.fromByte(buffer[0]);
        switch(mType){
            case EMPTY:
                return new EmptyMessage(getOrigin());
            case DATA:
            case ECHO:
            case BROADCAST:
                return new GenericDataMessage(buffer, getOrigin());
            case ERROR:
                return new ErrorMessage(buffer, getOrigin());
        }
        throw new UnknownMessageTypeException();
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public BufferMessageBuilder setBuffer(byte[] buffer) {
        this.buffer = buffer;
        return this;
    }
}