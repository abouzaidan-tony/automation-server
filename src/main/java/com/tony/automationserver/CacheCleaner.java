package com.tony.automationserver;

import java.util.Map;

import com.tony.sqlhelper.Repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CacheCleaner extends Thread {

    private static Logger logger = LogManager.getLogger(CacheCleaner.class.getName());

    @Override
    public void run() {
        while (true) {
            try {
                for (Map<Object, Object> var : Repository.getCaches().values())
                    var.clear();
                Thread.sleep(120000);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }
}