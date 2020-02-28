package com.tony.automationserver.statemachine;

import com.tony.automationserver.ClientSession;

abstract class State {
    protected byte[] data;
    protected final ClientSession session;

    public State(ClientSession session) {
        data = null;
        this.session = session;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public abstract State Process();

    public abstract boolean instantExecution();
}