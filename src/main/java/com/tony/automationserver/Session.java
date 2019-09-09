package com.tony.automationserver;

import java.io.IOException;
import java.net.Socket;

import com.tony.automationserver.streams.OnMessageReadyListener;
import com.tony.automationserver.streams.StreamManager;

import java.io.InputStreamReader;
import java.io.OutputStream;

public abstract class Session extends Thread implements OnMessageReadyListener {

    private InputStreamReader input;
    private Socket socket;
    private OutputStream out;
    private OnDataReceivedListener dataReceivedListener;
    private boolean running;
    private boolean stopListening;
    private StreamManager manager;

    public Session(Socket socket, StreamManager manager) {
        running = true;
        this.socket = socket;
        stopListening = false;
        this.manager = manager;
        this.manager.setOnMessageReadyListener(this);
        setOnDataReceivedListener(manager);
        try {
            out = socket.getOutputStream();
        } catch (IOException ex) {
            running = false;
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setOnDataReceivedListener(OnDataReceivedListener dataReceivedListener) {
        this.dataReceivedListener = dataReceivedListener;
    }

    public void close() {
        running = false;
        try {
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("Session Closed");
        OnSessionClosed();
    }

    public abstract void OnSessionClosed();

    public boolean isRunning() {
        return running;
    }

    public void StopListening(){
        stopListening = true;
    }

    public void sendMessage(byte[] msg) throws IOException {
        if(msg == null)
            return;
        try {
            out.write(manager.formatStream(msg));
        } catch (IOException e) {
            running = false;
            close();
            throw e;
        }

    }

    public void writeByte(byte b){
        try {
            out.write(b);
        } catch (IOException e){
            running = false;
            close();
        }
    }

    public final void run() {
        try {
            input = new InputStreamReader(socket.getInputStream());
        } catch (IOException ex) {
            running = false;
        }

        int length;
        char [] buffer = new char[256];
        while (running) {
            try {
                length = input.read(buffer, 0, buffer.length);

                if(length < 0)
                    throw new IOException();

                if(stopListening)
                    continue;

                if (dataReceivedListener != null);
                    dataReceivedListener.OnDataReceived(buffer, length);

            } catch (IOException ex) {
                running = false;
            }
        }
        close();
    }

    
}