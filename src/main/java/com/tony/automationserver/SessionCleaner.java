package com.tony.automationserver;

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
                    session.sendMessage(null);
                    if(!session.isRunning())
                        offlineSessions.add(session);
                }

                for (Session var : offlineSessions) {
                    Session.sessions.remove(var);
                }
                Session.lock.release();

                Thread.sleep(120000);

            }catch(Exception ex){
                
            }
        }
    }
}