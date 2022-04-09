package com.tony.automationserver.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PropertiesHelper {

    private static Logger log = LogManager.getLogger(PropertiesHelper.class.getName());

    public static Properties read(String path) {

        Properties prop = new Properties();
        try (InputStream inputStream = PropertiesHelper.class.getClassLoader().getResourceAsStream(path)) {
            prop.load(inputStream);
        } catch (IOException e) {
            log.error("Could not load properties file", e);
        }

        return prop;
    }
}
