package com.tony.automationserver;

import java.io.IOException;
import java.util.LinkedList;

public class SessionCleaner extends Thread {

    @Override
    public void run() {
        LinkedList<Session> offlineSessions = new LinkedList<>(); 
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

                for (Session var : offlineSessions) {
                    Session.sessions.remove(var);
                }
                
                Session.lock.release();

                Thread.sleep(1000);

            }
            catch(Exception ex){
                ex.printStackTrace();
                
            }
        }
    }
}