package com.tony.automationserver;

import java.util.PriorityQueue;

import com.tony.automationserver.settings.Settings;

public class SessionPool {

    private static final int MAX_THREADS = Settings.getInstance().getInt("maxThreadNumber");
    private static SessionPool pool;


    private PriorityQueue<SessionsThread> queue;

    private SessionPool() {
        queue = new PriorityQueue<>();
        init();
    }

    private void init(){
        for(int i=0; i<MAX_THREADS; i++){
            SessionsThread thread = new SessionsThread();
            thread.start();
            queue.add(thread);
        }
    } 

    public static synchronized SessionPool getInstance(){
        if(pool == null)
            pool = new SessionPool();
        return pool;
    }

    public synchronized void addSession(Session session){
        SessionsThread thread;
        
        if(queue.size() < MAX_THREADS)
        {
            thread = new SessionsThread();
            thread.start();
        }
        else
            thread = queue.poll();
        
        thread.registerSession(session);

        System.out.println(thread.getName() + " " + thread.getSize());

        queue.add(thread);
    }
}