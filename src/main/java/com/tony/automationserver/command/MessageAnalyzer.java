package com.tony.automationserver.command;

import com.tony.automationserver.exception.AutomationServerException;
import com.tony.automationserver.messages.Message;

public interface MessageAnalyzer {

    public abstract void Process(Message message) throws AutomationServerException;
}