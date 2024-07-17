package org.xml_to_db.config;

import lombok.extern.slf4j.Slf4j;
import org.xml_to_db.core.handlers.ErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is responsible for loading the application properties
 * from the application.properties file and then overriding them with the System environment variables.
 * This helps in maintaining the secrecy. It is implemented as a thread-safe singleton.
 */
@Slf4j
public class ConfigLoader {
    private static final String PROPERTIES_FILE = "/application.properties";
    private final Properties properties;

    private ConfigLoader() {
        properties = new Properties();
        loadProperties();
    }

    public static ConfigLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private void loadProperties() {
        String env = System.getProperty("env", "dev");
        String propertiesFile = String.format("/application-%s.properties", env);
        try (InputStream input = ConfigLoader.class.getResourceAsStream(propertiesFile)) {
            if (input == null) {
                throw new IOException("Unable to find " + PROPERTIES_FILE);
            }
            properties.load(input);
        } catch (IOException ex) {
            ErrorHandler.handleException("Error loading properties file: {}", ex);
        }
    }

    public String getProperty(String key) {
        String property = properties.getProperty(key);
        if (property == null) {
            log.warn("Property '{}' not found in properties file", key);
            return null;
        }
        String envValue = System.getenv(property);
        if (envValue != null) {
            return envValue;
        }
        log.info("Using property '{}' from properties file", key);
        return property;
    }

    public int getIntProperty(String key) {
        String property = getProperty(key);
        if (property == null) {
            throw new IllegalArgumentException("Property '" + key + "' not found");
        }
        try {
            return Integer.parseInt(property);
        } catch (NumberFormatException e) {
            log.error("Unable to parse property '{}' as integer: {}", key, property);
            throw new IllegalArgumentException("Property '" + key + "' is not a valid integer", e);
        }
    }

    private static class SingletonHolder {
        private static final ConfigLoader INSTANCE = new ConfigLoader();
    }
}
