package com.tony.automationserver.messages;


public class EmptyMessage extends Message{
    
    @Override
    public byte[] toByteArray() {
        return null;
    }
}