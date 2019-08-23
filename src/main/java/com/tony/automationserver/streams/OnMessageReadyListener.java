package com.tony.automationserver.streams;

public interface OnMessageReadyListener {
    public void OnMessageReady(byte[] message);
}