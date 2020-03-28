package com.tony.automationserver;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SessionPool {

    private static final int MAX_THREADS = 15;
    private static Logger logger = LogManager.getLogger(SessionPool.class);
    private static SessionPool pool;

    private ArrayList<SessionsThread> queue;
    private PausableThread [] pausableThreads;

    private SessionPool(PausableThread [] pausableThreads){
        queue = new ArrayList<>();
        this.pausableThreads = pausableThreads;
        init();
    }

    private void init() {
        // for(int i=0; i<1; i++){
        // SessionsThread thread = new SessionsThread();
        // thread.start();
        // queue.add(thread);
        // }
    }

    public static synchronized SessionPool getInstance(PausableThread[] pausableThreads) {
        if (pool == null)
            pool = new SessionPool(pausableThreads);
        return pool;
    }

    public synchronized void addSession(Session session) {
        logger.debug("Thread count " + queue.size());
        if (queue.size() == 0) {
            SessionsThread thread = new SessionsThread(pausableThreads);
            queue.add(thread);
            thread.registerSession(session);
            thread.start();
        } else {
            Collections.sort(queue);
            logger.debug("Sorted List");
            for (SessionsThread s : queue) {
                logger.debug(s.getSize() + " " + s.getName());
            }
            SessionsThread thread = queue.get(0);
            if (thread.getSize() == 0 || queue.size() >= MAX_THREADS) {
                thread.registerSession(session);
            } else {
                thread = new SessionsThread(pausableThreads);
                queue.add(thread);
                thread.registerSession(session);
                thread.start();
            }
        }
    }
}