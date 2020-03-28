package com.tony.automationserver;

import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PausableThread extends Thread {

    private static Logger log = LogManager.getLogger(PausableThread.class);

    private boolean isHalted = false;
    private Semaphore halter = new Semaphore(0);

    public abstract void process() throws InterruptedException;

    @Override
    public void run() {
       
        while (true) {
            try {

                if(isHalted){
                    log.debug("Halting Thread " + this.getClass().getSimpleName());
                    halter.acquire();
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

    public void unhalt(){
        log.debug("Resuming " + this.getClass().getSimpleName());
        isHalted = false;
        halter.release();
    }
}