package com.tony.automationserver;

public interface OnDataReceivedListener {
    public void OnDataReceived(byte[] buffer, int length);
}