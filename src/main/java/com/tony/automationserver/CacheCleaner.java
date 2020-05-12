package com.tony.automationserver;

import com.tony.sqlhelper.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CacheCleaner extends PausableThread {

    private static Logger logger = LogManager.getLogger(CacheCleaner.class);

    @Override
    public void process() throws InterruptedException{
        logger.debug("Cleaning cache");
        EntityManager.GetInstance().invalidateCache();
        Thread.sleep(130000);
    }
}