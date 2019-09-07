package com.tony.automationserver.statemachine;

import com.tony.automationserver.ClientSession;

public class FinalState extends State {

    public FinalState(ClientSession session) {
        super(session);
    }
    
    @Override
    public State Process() {
        System.out.println("Closing Session");
        session.close();
        return null;
    }

    @Override
    public boolean instantExecution() {
        return true;
    }

}