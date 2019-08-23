package com.tony.automationserver.streams;

import com.tony.automationserver.OnDataReceivedListener;

public abstract class StreamManager implements OnDataReceivedListener {

    protected OnMessageReadyListener messageReadyListener;

    public void setOnMessageReadyListener(OnMessageReadyListener listener) {
        messageReadyListener = listener;
    }

    public abstract byte[] formatStream(byte[] msg);
}