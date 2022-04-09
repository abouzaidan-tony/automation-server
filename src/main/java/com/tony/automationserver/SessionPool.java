package com.tony.automationserver;

import java.util.ArrayList;
import java.util.Collections;

import com.tony.automationserver.PausableThread.SessionsThreadEvents;
import com.tony.automationserver.domain.Config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SessionPool implements SessionsThreadEvents {

    private static Config config = Config.GetInstance();
    private static final int MAX_THREADS = config.getMaxSessionThreadCount();
    private static Logger logger = LogManager.getLogger(SessionPool.class);
    private static SessionPool pool;

    private ArrayList<SessionsThread> queue;
    private PausableThread [] pausableThreads;

    private boolean allPaused;

    private SessionPool(PausableThread [] pausableThreads){
        this.pausableThreads = pausableThreads;
        init();
    }

    private void init() {
        queue = new ArrayList<>();
        allPaused = true;
    }

    public static synchronized SessionPool getInstance(PausableThread[] pausableThreads) {
        if (pool == null)
            pool = new SessionPool(pausableThreads);
        return pool;
    }

    public synchronized void addSession(Session session) {
        logger.debug("Thread count " + queue.size());
        if (queue.size() == 0) {
            SessionsThread thread = new SessionsThread(this);
            queue.add(thread);
            thread.registerSession(session);
            resume();
        } else {
            Collections.sort(queue);
            logger.trace("Sorted List");
            for (SessionsThread s : queue) {
                logger.trace(s.getSize() + " " + s.getName());
            }
            SessionsThread thread = queue.get(0);
            if (thread.getSize() == 0 || queue.size() >= MAX_THREADS) {
                thread.registerSession(session);
            } else {
                thread = new SessionsThread(this);
                queue.add(thread);
                thread.registerSession(session);
            }
        }
    }

    public synchronized boolean isAllPaused() {
        allPaused = true;
        for(SessionsThread t : queue){
            if(!t.isPaused()){
                allPaused = false;
                return allPaused;
            }
        }
        return allPaused;
    }

    @Override
    public void onPaused() {
        if(!isAllPaused())
            return;
        pause();
    }

    private void pause() {
        for (int i = 0; i < pausableThreads.length; i++) {
            pausableThreads[i].halt();
        }
    }

    private void resume(){
        if(!allPaused)
            return;
        allPaused = false;
        for (int i = 0; i < pausableThreads.length; i++) {
            pausableThreads[i].unhalt();
        }
    }

    @Override
    public void onResumed() {
        resume();
    }

    @Override
    public void onStarted() {
       resume();
    };
}