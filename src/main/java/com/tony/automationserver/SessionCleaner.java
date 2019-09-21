package com.tony.automationserver;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SessionCleaner extends Thread {

    private static Logger logger = LogManager.getLogger(SessionCleaner.class.getName());

    private int sleepTime;

    @Override
    public void run() {
        LinkedList<Session> offlineSessions = new LinkedList<>(); 
        sleepTime = 1000;
        while(true){
            try{
                offlineSessions.clear();

                Session.lock.acquire();
                
                for(Session session : Session.sessions){
                    try{
                        session.sendMessage(null);
                    }catch(IOException ex){}

                    if(!session.isRunning())
                        offlineSessions.add(session);
                }
                
                
                if(offlineSessions.size() == 0)
                    sleepTime += 100;
                else {
                    logger.debug(() -> "Removing " + offlineSessions.size() + " sessions");
                    sleepTime -= offlineSessions.size() * 100;
                }

                if(sleepTime > 7000)
                    sleepTime = 7000;
                else if (sleepTime < 2000)
                    sleepTime = 2000;
                
                for (Session var : offlineSessions) {
                    Session.sessions.remove(var);
                }
                
                Session.lock.release();

                Thread.sleep(sleepTime);
            }
            catch(Exception ex){
                logger.error(ex.getMessage(), ex);
            }
        }
    }
}