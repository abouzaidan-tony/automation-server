package com.tony.automationserver.command;

import com.tony.automationserver.messages.Message;

public interface MessageAnalyzer {

    public abstract void Process(Message message);
}