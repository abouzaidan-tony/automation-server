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

    private SessionPool() {
        queue = new ArrayList<>();
        init();
    }

    private void init() {
        // for(int i=0; i<1; i++){
        // SessionsThread thread = new SessionsThread();
        // thread.start();
        // queue.add(thread);
        // }
    }

    public static synchronized SessionPool getInstance() {
        if (pool == null)
            pool = new SessionPool();
        return pool;
    }

    public synchronized void addSession(Session session) {
        logger.debug("Thread count " + queue.size());
        if (queue.size() == 0) {
            SessionsThread thread = new SessionsThread();
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
                thread = new SessionsThread();
                queue.add(thread);
                thread.registerSession(session);
                thread.start();
            }
        }
    }
}