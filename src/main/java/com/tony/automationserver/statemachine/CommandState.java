package com.tony.automationserver.statemachine;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.command.MessageAnalyzer;
import com.tony.automationserver.exception.AutomationServerException;
import com.tony.automationserver.messages.BufferMessageBuilder;
import com.tony.automationserver.messages.ErrorMessageBuilder;
import com.tony.automationserver.messages.Message;
import com.tony.automationserver.messages.Message.MessageType;

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

            if (message.getMessageType() == MessageType.ECHO) {
                message.setOrigin(session.getClient().getKey());
                session.sendMessage(message.toByteArray());
            } else {
                analyzer.Process(message);
            }
            message = null;

        }catch (AutomationServerException ex) {
            try{
                log.info(session.getClient() + " -> " + message.getOrigin(), ex);
                message = new ErrorMessageBuilder().setException(ex).setOrigin(session.getClient()).build();
                session.sendMessage(message.toByteArray());
                
            }catch(AutomationServerException ex2){
                log.error(ex2);
                nextState = new FinalState(session);
            }
        }
        return nextState;
    }

    @Override
    public boolean instantExecution() {
        return false;
    }

}