package com.tony.automationserver;

import java.util.Map;

import com.tony.automationserver.sqlhelper.Repository;
import com.tony.automationserver.sqlhelper.SQLObject;

public class CacheCleaner extends Thread {

    @Override
    public void run() {
        while(true){
            try{
                for (Map<Object, SQLObject> var : Repository.getCaches().values())
                    var.clear();
                Thread.sleep(120000);
            }catch(Exception ex){
                
            }
        }
    }
}