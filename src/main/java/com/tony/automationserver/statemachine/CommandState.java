package com.tony.automationserver.statemachine;

import java.io.IOException;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.command.MessageAnalyzer;
import com.tony.automationserver.exception.DeviceNotConnectedException;
import com.tony.automationserver.exception.DeviceNotFoundException;
import com.tony.automationserver.messages.Message;
import com.tony.automationserver.messages.MessageBuilder;
import com.tony.automationserver.messages.Message.MessageType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandState extends State {

    private static Logger logger = LogManager.getLogger(CommandState.class.getName());
    private MessageAnalyzer analyzer;

    public CommandState(ClientSession session, MessageAnalyzer analyzer) {
        super(session);
        this.analyzer = analyzer;
    }

    @Override
    public State Process() {
        Message message = null;
        State nextState = this;

        logger.debug(() -> "Processing command from " + session.getClient());

        try{
            message = new Message(data, session.getClient());
            
            if(!message.KeepAlive())
                nextState = new FinalState(session);

            if(message.getMessageType() == MessageType.ECHO) {
                message.setOrigin(session.getClient().getKey());
                session.sendMessage(message.toByteArray());
            } else {
                analyzer.Process(message);
            }

        }catch(IllegalArgumentException ex){
            logger.warn(() -> "Wrong message structure " + session.getClient());
            message = new MessageBuilder()
                .setOrigin(session.getClient().getKey())
                .setMessage("Wrong Message Format")
                .setMessageType(MessageType.ERROR)
                .build();

        }catch(DeviceNotConnectedException | DeviceNotFoundException ex) {
            final String str = session.getClient() + " -> " + message.getOrigin();
            logger.info(() -> ex.getMessage() + " " + str);
            message = new MessageBuilder().setOrigin(message.getOrigin())
                    .setMessage(ex.getMessage()).setMessageType(MessageType.ERROR).build();
        }catch(IOException ex){
            logger.error(session.getClient() + " " + ex.getMessage(), ex);
        }

        try{
            if(message != null)
                session.sendMessage(message.toByteArray());
        }catch(IOException ex){
            logger.error(session.getClient() + " " + ex.getMessage(), ex);
        }
        
        return nextState;    
    }

    @Override
    public boolean instantExecution() {
        return false;
    }

}