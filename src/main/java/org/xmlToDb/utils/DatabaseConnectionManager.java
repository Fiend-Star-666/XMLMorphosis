package org.xmlToDb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseConnectionManager {
    private static final DatabaseConnectionManager INSTANCE = new DatabaseConnectionManager();
    private final Map<String, ThreadLocal<DatabaseConnection>> connectionHolders = new ConcurrentHashMap<>();
    private final Properties dbProperties = new Properties();

    private DatabaseConnectionManager() {
        loadDatabaseProperties();
    }

    public static DatabaseConnectionManager getInstance() {
        return INSTANCE;
    }

    private void loadDatabaseProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("Unable to find database.properties");
            }
            dbProperties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    public DatabaseConnection getConnection(String schema) throws SQLException {
        ThreadLocal<DatabaseConnection> holder = connectionHolders.computeIfAbsent(schema, k -> new ThreadLocal<>());
        DatabaseConnection connection = holder.get();
        if (connection == null) {
            DatabaseConfiguration config = createDatabaseConfiguration(schema);
            try {
                connection = new DatabaseConnection(config);
                holder.set(connection);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Failed to load database driver", e);
            }
        }
        return connection;
    }

    private DatabaseConfiguration createDatabaseConfiguration(String schema) {
        String url = getUrlForSchema(schema);
        String username = getUsernameForSchema(schema);
        String password = getPasswordForSchema(schema);
        String driverClassName = getDriverClassNameForSchema(schema);

        return new DatabaseConfiguration(url, username, password, driverClassName);
    }

    public void closeConnection(String schema) throws SQLException {
        ThreadLocal<DatabaseConnection> holder = connectionHolders.get(schema);
        if (holder != null) {
            DatabaseConnection connection = holder.get();
            if (connection != null) {
                connection.close();
                holder.remove();
            }
        }
    }

    private String getUrlForSchema(String schema) {
        String url = dbProperties.getProperty(schema + ".url");
        if (url == null) {
            url = dbProperties.getProperty("default.url");
        }
        return url + schema;
    }

    private String getUsernameForSchema(String schema) {
        return dbProperties.getProperty(schema + ".username",
                dbProperties.getProperty("default.username"));
    }

    private String getPasswordForSchema(String schema) {
        return dbProperties.getProperty(schema + ".password",
                dbProperties.getProperty("default.password"));
    }

    private String getDriverClassNameForSchema(String schema) {
        return dbProperties.getProperty(schema + ".driver",
                dbProperties.getProperty("default.driver"));
    }
}
