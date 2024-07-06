package org.xmlToDb.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is responsible for loading the application properties
 * from the application.properties file and then overriding them with the System environment variables.
 * This helps in maintaining the secrecy.
 */
@Slf4j
public class ConfigLoader {
    private static final String PROPERTIES_FILE = "/application.properties";
    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getResourceAsStream(PROPERTIES_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading properties file.", ex);
        }
    }

    public static String getProperty(String key) {
        String property = properties.getProperty(key);
        return System.getenv(property);
    }

    public static int getIntProperty(String key) {
        String property = getProperty(key);
        return Integer.parseInt(System.getenv(property));
    }

    private ConfigLoader() {
    }
}
