package com.tony.automationserver;

import java.util.LinkedList;

import com.tony.automationserver.exception.AutomationServerException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SessionCleaner extends PausableThread {

    private static Logger log = LogManager.getLogger(SessionCleaner.class);

    private int sleepTime;

    private LinkedList<Session> offlineSessions = new LinkedList<>();

    @Override
    public void process() throws InterruptedException {

        offlineSessions.clear();

        Session.lock.acquire();

        for (Session session : Session.sessions) {
            try {
                if (!session.isSkip())
                    session.sendMessage(null);
            } catch (AutomationServerException ex) {
            }

            if (!session.isRunning())
                offlineSessions.add(session);
        }

        if (offlineSessions.size() == 0)
            sleepTime += 100;
        else {
            log.debug("Removing " + offlineSessions.size() + " sessions");
            sleepTime -= offlineSessions.size() * 100;
        }

        if (sleepTime > 7000)
            sleepTime = 7000;
        else if (sleepTime < 2000)
            sleepTime = 2000;

        for (Session var : offlineSessions) {
            Session.sessions.remove(var);
        }

        Session.lock.release();

        Thread.sleep(sleepTime);
    }
}