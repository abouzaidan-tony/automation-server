package com.tony.automationserver;

import java.util.LinkedList;

public class SessionCleaner extends Thread {

    @Override
    public void run() {
        LinkedList<ClientSession> offlineSessions = new LinkedList<>(); 
        while(true){
            try{
                offlineSessions.clear();
                for (ClientSession var : ClientSession.getDevicesSessions().values()) {
                    var.sendMessage(null);
                    if(!var.isRunning())
                        offlineSessions.add(var);
                }

                for (ClientSession var : ClientSession.getUserSessions().values()) {
                    var.sendMessage(null);
                    if(!var.isRunning())
                        offlineSessions.add(var);
                }

                for (ClientSession var : offlineSessions) {
                    var.removeFromList();
                }

                offlineSessions.clear();

                Thread.sleep(120000);

            }catch(Exception ex){
                
            }
        }
    }
}