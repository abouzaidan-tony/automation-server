package com.tony.automationserver;

import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PausableThread extends Thread {

    private static Logger log = LogManager.getLogger(PausableThread.class);

    private boolean started;

    public interface SessionsThreadEvents {
        public void onPaused();
        public void onResumed();
        public void onStarted();
    }

    private SessionsThreadEvents sessionsThreadListener;

    public PausableThread(){
        this.sessionsThreadListener = null;
        paused = false;
        started = false;
    }

    public void setSessionsThreadlistener(SessionsThreadEvents sessionsThreadlistener) {
        this.sessionsThreadListener = sessionsThreadlistener;
    }

    public SessionsThreadEvents getSessionsThreadlistener() {
        return sessionsThreadListener;
    }

    private boolean isHalted = false;
    private Semaphore halter = new Semaphore(0);
    private boolean paused;

    public abstract void process() throws InterruptedException;

    public void preProcess() {

    }

    
    @Override
    public void run() {
       
        if(sessionsThreadListener != null)
            sessionsThreadListener.onStarted();
            
        preProcess();

        while (true) {
            try {

                synchronized (this){
                    if (isHalted)
                        selfHalt();
                }

                process();
                
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                break;
            }
        }
    }

    public void halt(){
        isHalted = true;
    }

    private synchronized void pause(){
        log.debug("Halting Thread " + this.getClass().getSimpleName());
        paused = true;
        isHalted = true;
    }

    protected void selfHalt() throws InterruptedException {
        pause();
        if (sessionsThreadListener != null)
            sessionsThreadListener.onPaused();
        halter.acquire();
        isHalted = false;
        paused = false;
        if (sessionsThreadListener != null)
            sessionsThreadListener.onResumed();
    }

    public void unhalt(){
        if(!started){
            started = true;
            paused = false;
            start();
        }
        if(!isPaused())
            return;
        log.debug("Resuming " + this.getClass().getSimpleName());
        isHalted = false;
        halter.release();
    }

    public boolean isPaused() {
        return paused;
    }

    public SessionsThreadEvents getSessionsThreadListener() {
        return sessionsThreadListener;
    }

    public void setSessionsThreadListener(SessionsThreadEvents sessionsThreadListener) {
        this.sessionsThreadListener = sessionsThreadListener;
    }
}