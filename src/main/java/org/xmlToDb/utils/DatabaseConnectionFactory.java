package org.xmlToDb.utils;

import org.xmlToDb.config.ConfigLoader;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConnectionFactory {
    private static final Map<String, DatabaseConfiguration> configurations = new HashMap<>();

    static {
        ConfigLoader configLoader = ConfigLoader.getInstance();

        // Load database configurations from properties
        String[] dbConfigs = configLoader.getProperty("DB_CONFIGURATIONS").split(",");
        for (String config : dbConfigs) {
            String[] parts = config.split(":");
            String key = parts[0];
            String url = configLoader.getProperty("DB_URL_" + key);
            String username = configLoader.getProperty("DB_USERNAME_" + key);
            String password = configLoader.getProperty("DB_PASSWORD_" + key);
            String driverClassName = configLoader.getProperty("DB_DRIVER_" + key);
            configurations.put(key, new DatabaseConfiguration(url, username, password, driverClassName));
        }
    }

    public static DatabaseConnection getConnection(String xmlPath, String xsdPath) throws SQLException, ClassNotFoundException {
        // Logic to determine which database configuration to use based on XML and XSD paths
        // This could be based on file names, content, or other criteria
        // For now, we'll use a simple mapping based on file names
        String key = xmlPath.substring(xmlPath.lastIndexOf('/') + 1) + ":" + xsdPath.substring(xsdPath.lastIndexOf('/') + 1);
        DatabaseConfiguration config = configurations.get(key);
        if (config == null) {
            throw new IllegalArgumentException("No database configuration found for " + key);
        }
        return new DatabaseConnection(config);
    }
}