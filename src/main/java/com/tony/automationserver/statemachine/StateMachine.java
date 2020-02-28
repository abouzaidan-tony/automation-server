package com.tony.automationserver.statemachine;

import com.tony.automationserver.ClientSession;

public class StateMachine {

    private State currentState;
    protected ClientSession session;

    public StateMachine(ClientSession session) {
        this.session = session;
        currentState = new AuthenticationState(session);
    }

    public void Process(byte[] message) {
        currentState.setData(message);
        do {
            State resultState = currentState.Process();
            if (resultState != null)
                currentState = resultState;
            else
                break;
        } while (currentState.instantExecution());
    }
}