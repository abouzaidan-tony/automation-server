package com.tony.automationserver.domain;

import java.util.Properties;

import com.tony.automationserver.utils.PropertiesHelper;

public class Config {

    public static final Properties properties;
    public static final Config config;
    
    static {
        properties = PropertiesHelper.read(Constants.propertiesFileName);
        config = new Config();
    }

    public static Config GetInstance(){
        return config;
    }

    private int serverPort;
    private int sessionMinSleepTime;
    private int sessionMaxSleepTime;
    private int maxSessionThreadCount;
    private int cacheCleanerSleepTime;
    private int sessionCleanerMinSleepTime;
    private int sessionCleanerMaxSleepTime;

    private Config(){
        init();
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getCacheCleanerSleepTime(){
        return cacheCleanerSleepTime;
    }

    public int getSessionCleanerMinSleepTime() {
        return sessionCleanerMinSleepTime;
    }

    public int getSessionCleanerMaxSleepTime() {
        return sessionCleanerMaxSleepTime;
    }

    public int getMaxSessionThreadCount() {
        return maxSessionThreadCount;
    }

    public int getSessionMinSleepTime() {
        return sessionMinSleepTime;
    }

    public int getSessionMaxSleepTime() {
        return sessionMaxSleepTime;
    }

    public void init() {
        serverPort = Integer.valueOf(properties.getProperty(Constants.propertiesServerPort));
        cacheCleanerSleepTime = Integer.valueOf(properties.getProperty(Constants.propertiesCacheCleanerSleepTime));
        sessionCleanerMinSleepTime = Integer.valueOf(properties.getProperty(Constants.propertiesSessionCleanerMinSleepTime));
        sessionCleanerMaxSleepTime = Integer.valueOf(properties.getProperty(Constants.propertiesSessionCleanerMaxSleepTime));
        maxSessionThreadCount = Integer.valueOf(properties.getProperty(Constants.propertiesSessionMaxThreadCount));
        sessionMinSleepTime = Integer.valueOf(properties.getProperty(Constants.propertiesSessionMinSleepTime));
        sessionMaxSleepTime = Integer.valueOf(properties.getProperty(Constants.propertiesSessionMaxSleepTime));

    }
}
