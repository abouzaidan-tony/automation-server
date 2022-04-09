package com.tony.automationserver;

import com.tony.automationserver.domain.Config;
import com.tony.sqlhelper.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CacheCleaner extends PausableThread {

    private static Logger logger = LogManager.getLogger(CacheCleaner.class);
    private static Config config = Config.GetInstance();

    @Override
    public void process() throws InterruptedException{
        logger.trace("Cleaning cache");
        EntityManager.GetInstance().invalidateCache();
        Thread.sleep(config.getCacheCleanerSleepTime());
    }
}