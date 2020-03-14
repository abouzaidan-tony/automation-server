package com.tony.automationserver;

import com.tony.sqlhelper.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CacheCleaner extends Thread {

    private static Logger logger = LogManager.getLogger(CacheCleaner.class);

    @Override
    public void run() {
        while (true) {
            try {
                logger.debug("Cleaning cache");
                EntityManager.GetInstance().invalidateCache();
                Thread.sleep(120000);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }
}