package com.tony.automationserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.tony.automationserver.exception.AutomationServerException;
import com.tony.automationserver.exception.IOServerException;
import com.tony.automationserver.streams.OnMessageReadyListener;
import com.tony.automationserver.streams.StreamManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.OutputStream;

public abstract class Session implements OnMessageReadyListener {

    private static Logger logger = LogManager.getLogger(Session.class);
    public static Semaphore lock = new Semaphore(1);
    public static List<Session> sessions = new LinkedList<>();

    private InputStream input;
    private Socket socket;
    private OutputStream out;
    private OnDataReceivedListener dataReceivedListener;
    private boolean running;
    private StreamManager manager;
    private int skip;
    protected boolean authenticated;
    protected String name;

    public Session(Socket socket, StreamManager manager) {
        running = true;
        skip = 0;
        this.socket = socket;
        this.manager = manager;
        authenticated = false;
        this.manager.setOnMessageReadyListener(this);
        setOnDataReceivedListener(manager);
        try {
            out = socket.getOutputStream();
            input = socket.getInputStream();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            close();
            return;
        }
        try {
            lock.acquire();
            sessions.add(this);
            lock.release();
        } catch (InterruptedException ex) {
            lock.release();
            logger.error(ex.getMessage(), ex);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setOnDataReceivedListener(OnDataReceivedListener dataReceivedListener) {
        this.dataReceivedListener = dataReceivedListener;
    }

    public synchronized void close() {
        if (!running)
            return;
        running = false;
        try {
            socket.close();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        logger.debug("Session closed");
        OnSessionClosed();
    }

    public abstract void OnSessionClosed();

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void sendMessage(byte[] msg) throws AutomationServerException {
        try {
            out.write(manager.formatStream(msg));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            close();
            throw new IOServerException(e);
        }
    }

    public synchronized void writeByte(byte b) {
        try {
            out.write(b);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            close();
        }
    }

    public OnDataReceivedListener getDataReceivedListener() {
        return dataReceivedListener;
    }

    public InputStream getInputStream() {
        return input;
    }

    public boolean isSkip() {
        boolean skip = this.skip++ <= 3;
        return skip;
    }

    public boolean isAuthenticated(){
        return authenticated;
    }

    public abstract String getSessionName();
}