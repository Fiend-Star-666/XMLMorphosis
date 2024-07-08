package org.xml_to_db.database;

import lombok.extern.slf4j.Slf4j;
import org.xml_to_db.config.ConfigLoader;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DatabaseConnectionFactory {
    private static final Map<String, DatabaseConfiguration> configurations = new HashMap<>();
    private static final ConfigLoader configLoader = ConfigLoader.getInstance();

    static {
        loadConfigurations();
    }

    private DatabaseConnectionFactory() {
        // Private constructor to prevent instantiation
    }

    private static void loadConfigurations() {
        String[] dbConfigs = configLoader.getProperty("DB_CONFIGURATIONS").split(",");
        for (String config : dbConfigs) {
            String[] parts = config.split(":");
            String key = parts[0];
            String url = configLoader.getProperty("DB_URL_" + key);
            String username = configLoader.getProperty("DB_USERNAME_" + key);
            String password = configLoader.getProperty("DB_PASSWORD_" + key);
            String driverClassName = configLoader.getProperty("DB_DRIVER_" + key);
            configurations.put(key, new DatabaseConfiguration(url, username, password, driverClassName));
            log.info("Loaded database configuration for key: {}", key);
        }
    }

    public static DatabaseConnection getConnection(String xmlPath, String xsdPath) throws SQLException, ClassNotFoundException {
        String key = determineConfigurationKey(xmlPath, xsdPath);
        DatabaseConfiguration config = configurations.get(key);
        if (config == null) {
            log.error("No database configuration found for key: {}", key);
            throw new IllegalArgumentException("No database configuration found for " + key);
        }
        log.info("Creating database connection for key: {}", key);
        return new DatabaseConnection(config);
    }

    private static String determineConfigurationKey(String xmlPath, String xsdPath) {
        // This is a simple implementation. You might want to implement a more sophisticated
        // method to determine the configuration key based on your specific requirements.
        String xmlFileName = xmlPath.substring(xmlPath.lastIndexOf('/') + 1);
        String xsdFileName = xsdPath.substring(xsdPath.lastIndexOf('/') + 1);
        return xmlFileName + ":" + xsdFileName;
    }
}
