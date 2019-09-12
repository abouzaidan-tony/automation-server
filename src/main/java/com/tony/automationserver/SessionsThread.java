package com.tony.automationserver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class SessionsThread extends Thread implements Comparable<SessionsThread> {

    private LinkedList<Session> sessions;
    private Iterator<Session> iterator;
    private Queue<Session> queue;

    private Semaphore halter;

    public SessionsThread() {
        sessions = new LinkedList<>();
        queue = new LinkedList<>();
        halter = new Semaphore(1);
    }

    public synchronized void registerSession(Session s) {
        boolean wasEmpty = queue.size() == 0;
        queue.add(s);
        if(wasEmpty)
            halter.release();
    }
    
    public synchronized int getSize(){
        return sessions.size();
    }

    @Override
    public void run() {

        int length;
        byte[] buffer = new byte[256];
 
        BufferedInputStream bf = null;

        while (true) {
            try {

                Thread.sleep(2);

                
                Session session = null;

                boolean isEmpty= true;

                synchronized (this){
                    for (Session s : queue) {
                        sessions.add(s);
                    }
                    queue.clear();
                    isEmpty = sessions.size() == 0;
                }

                if(isEmpty)
                    halter.acquire();


                iterator = sessions.iterator();

                do{
                    
                    synchronized(this){
                        if(iterator.hasNext())
                            session = iterator.next();
                        else
                            session = null;
                    }

                    if(session == null)
                        break;

                    if(!session.isRunning()){
                        synchronized(this){
                            iterator.remove();
                        }
                        continue;
                    }

                    InputStream input = session.getInputStream();
                    
                    bf = new BufferedInputStream(input);
                    try {

                        if(session.getSocket().isClosed())
                        {
                            synchronized(this){
                                iterator.remove();
                            }
                            continue;
                        }

                        if(bf.available() == 0)
                            continue;
                        
                        length = input.read(buffer, 0, buffer.length);
        
                        if(length < 0)
                            throw new IOException();
                        
                        if (session.getDataReceivedListener() != null);
                            session.getDataReceivedListener().OnDataReceived(buffer, length);
        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        session.close();
                        iterator.remove();
                    }

                }while(true);
              
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public int compareTo(SessionsThread o) {
        return Integer.compare(sessions.size(), o.sessions.size());
    }
}