package com.tony.automationserver.messages;

import com.tony.automationserver.client.Client;
import com.tony.automationserver.exception.AutomationServerException;

public abstract class MessageBuilder {

    private Client origin;

    public MessageBuilder() {

    }

    public abstract Message build() throws AutomationServerException;

    public Client getOrigin() {
        return origin;
    }

    public MessageBuilder setOrigin(Client origin) {
        this.origin = origin;
        return this;
    }
}