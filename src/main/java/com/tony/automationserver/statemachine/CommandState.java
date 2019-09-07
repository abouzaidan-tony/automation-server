package com.tony.automationserver.statemachine;

import java.io.IOException;

import com.tony.automationserver.ClientSession;
import com.tony.automationserver.command.MessageAnalyzer;
import com.tony.automationserver.exception.DeviceNotConnectedException;
import com.tony.automationserver.exception.DeviceNotFoundException;
import com.tony.automationserver.messages.Message;
import com.tony.automationserver.messages.MessageBuilder;
import com.tony.automationserver.messages.Message.MessageType;

public class CommandState extends State {

    private MessageAnalyzer analyzer;

    public CommandState(ClientSession session, MessageAnalyzer analyzer) {
        super(session);
        this.analyzer = analyzer;
    }

    @Override
    public State Process() {
        Message message = null;
        State nextState = this;
        try{
            message = new Message(data, session.getClient());
            
            if(message.getMessageType() == MessageType.ECHO) {
                message.setOrigin(session.getClient().getKey());
                session.sendMessage(message.toByteArray());
            } else {
                analyzer.Process(message);
            }

            if(!message.KeepAlive())
                nextState = new FinalState(session);

            message = null;
        }catch(IllegalArgumentException ex){

            message = new MessageBuilder()
                .setOrigin(session.getClient().getKey())
                .setMessage("Wrong Message Format")
                .setMessageType(MessageType.ERROR)
                .build();

        }catch(DeviceNotConnectedException | DeviceNotFoundException ex) {
            message = new MessageBuilder().setOrigin(message.getOrigin())
                    .setMessage(ex.getMessage()).setMessageType(MessageType.ERROR).build();
        }catch(IOException ex){

        }

        try{
            if(message != null)
            session.sendMessage(message.toByteArray());
        }catch(IOException ex){

        }
        
        System.out.println("Processing Command");
        return nextState;    
    }

    @Override
    public boolean instantExecution() {
        return false;
    }

}