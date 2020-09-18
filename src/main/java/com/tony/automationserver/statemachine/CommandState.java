package com.tony.automationserver.statemachine;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.command.MessageAnalyzer;
import com.tony.automationserver.exception.AutomationServerException;
import com.tony.automationserver.messages.BufferMessageBuilder;
import com.tony.automationserver.messages.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandState extends State {

    private static Logger log = LogManager.getLogger(CommandState.class);
    private MessageAnalyzer analyzer;

    public CommandState(ClientSession session, MessageAnalyzer analyzer) {
        super(session);
        this.analyzer = analyzer;
    }

    @Override
    public State Process() {
        Message message = null;
        State nextState = this;

        log.debug("Processing command from " + session.getClient());

        try {
            message = new BufferMessageBuilder().setBuffer(data).setOrigin(session.getClient()).build();

            log.debug("Message Type [" + message.getMessageType() + "]");

            analyzer.Process(message);

            message = null;
            log.debug("Message processing completed from [" + session.getClient().getKey() + "]");

        } catch (AutomationServerException ex) {
            log.error(ex.getMessage(), ex);
            nextState = new FinalState(session);
        }
        return nextState;
    }

    @Override
    public boolean instantExecution() {
        return false;
    }

}