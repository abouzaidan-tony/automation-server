package com.tony.automationserver;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.tony.automationserver.streams.OnMessageReadyListener;
import com.tony.automationserver.streams.StreamManager;

import java.io.InputStreamReader;
import java.io.OutputStream;

public abstract class Session extends Thread implements OnMessageReadyListener {

    public static Semaphore lock = new Semaphore(1);
    public static List<Session> sessions = new LinkedList<>();

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
        try {
            lock.acquire();
            sessions.add(this);
            lock.release();
        } catch (InterruptedException e) {
        }
        
    }

    public Socket getSocket() {
        return socket;
    }

    public void setOnDataReceivedListener(OnDataReceivedListener dataReceivedListener) {
        this.dataReceivedListener = dataReceivedListener;
    }

    public synchronized void close() {
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

    public synchronized boolean isRunning() {
        return running;
    }

    public void stopListening(){
        stopListening = true;
    }

    public synchronized void sendMessage(byte[] msg) throws IOException {
        try {
            out.write(manager.formatStream(msg));
        } catch (IOException e) {
            running = false;
            close();
            throw e;
        }

    }

    public synchronized void writeByte(byte b){
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
                synchronized(this){
                    running = false;
                }
            }
        }
        close();
    }

    
}