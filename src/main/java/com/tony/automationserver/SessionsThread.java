package com.tony.automationserver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.tony.automationserver.domain.Config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SessionsThread extends PausableThread implements Comparable<SessionsThread> {

    private static Logger logger = LogManager.getLogger(SessionsThread.class);
    private static Config config = Config.GetInstance();

    private LinkedList<Session> sessions;
    private Iterator<Session> iterator;
    private Queue<Session> queue;

    private int length;
    private byte[] buffer = new byte[256];
    private int sleepTime;
    private int maxSleepTime;
    private int noDataCount = 0;

    public SessionsThread(SessionsThreadEvents sessionsThreadListener) {
        sessions = new LinkedList<>();
        queue = new LinkedList<>();
        setSessionsThreadListener(sessionsThreadListener);
        maxSleepTime = config.getSessionMaxSleepTime();
        sleepTime = config.getSessionMinSleepTime();
    }

    public synchronized void registerSession(Session s) {
        logger.debug("Adding session to " + this.getName() + " size is " + (queue.size() + sessions.size()));
        queue.add(s);
        logger.debug("New size is " + (queue.size() + sessions.size()));
        this.unhalt();
    }

    public synchronized int getSize() {
        return sessions.size() + queue.size();
    }

    @Override
    public void preProcess() {
        logger.info("Sessions Thread Started");
    }

    @Override
    public void process() {

        BufferedInputStream bf = null;

        try {

            Thread.sleep(sleepTime);

            noDataCount = 0;

            Session session = null;

            boolean isEmpty = true;

            synchronized (this) {
                for (Session s : queue) {
                    sessions.add(s);
                }
                queue.clear();
                isEmpty = sessions.size() == 0;
            }

            if (isEmpty) {
                logger.debug("Sessions Emtpy, Halting");
                selfHalt();
                logger.debug("Resumed");
            }

            iterator = sessions.iterator();

            do {

                synchronized (this) {
                    if (iterator.hasNext())
                        session = iterator.next();
                    else
                        session = null;
                }

                if (session == null)
                    break;

                if (!session.isRunning()) {
                    synchronized (this) {
                        iterator.remove();
                        logger.debug(getName() + " removing session " + session.getSessionName() + ", new size "
                                + sessions.size());
                    }
                    continue;
                }

                InputStream input = session.getInputStream();

                bf = new BufferedInputStream(input);

                try {

                    if (!session.isRunning()) {
                        synchronized (this) {
                            iterator.remove();
                            logger.debug(getName() + " removing session, new size " + queue.size());
                        }
                        continue;
                    }

                    if (bf.available() == 0) {
                        noDataCount++;
                        continue;
                    }

                    length = input.read(buffer, 0, buffer.length);

                    if (length < 0)
                        throw new IOException();

                    if (session.getDataReceivedListener() != null)
                        session.getDataReceivedListener().OnDataReceived(buffer, length);

                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    session.close();
                    iterator.remove();
                }

            } while (true);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        int existingLength = 0;
        synchronized (this) {
            existingLength = sessions.size();
        }

        if (existingLength == noDataCount)
            sleepTime += 1;
        else
            sleepTime -= 500;

        if (sleepTime > maxSleepTime)
            sleepTime = maxSleepTime;
        else if (sleepTime < config.getSessionMinSleepTime())
            sleepTime = config.getSessionMinSleepTime();
    }

    @Override
    public int compareTo(SessionsThread o) {
        return Integer.compare(this.getSize(), o.getSize());
    }
}