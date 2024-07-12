package org.xml_to_db.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.xml_to_db.core.handlers.ErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages database connections for different schemas.
 */
@Slf4j
public class DatabaseConnectionManager {
    private static final DatabaseConnectionManager INSTANCE = new DatabaseConnectionManager();
    private final Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();
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
            log.info("Database properties loaded successfully");
        } catch (IOException e) {
            ErrorHandler.handleException("Failed to load database properties", e);
        }
    }

    public DatabaseConnection getConnection(String schema) throws SQLException, ClassNotFoundException {
        HikariDataSource dataSource = dataSources.computeIfAbsent(schema, this::createDataSource);
        return new DatabaseConnection((DatabaseConfiguration) dataSource.getConnection());
    }

    private HikariDataSource createDataSource(String schema) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getUrlForSchema(schema));
        config.setUsername(getUsernameForSchema(schema));
        config.setPassword(getPasswordForSchema(schema));
        config.setDriverClassName(getDriverClassNameForSchema(schema));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(10000);
        return new HikariDataSource(config);
    }

    private DatabaseConfiguration createDatabaseConfiguration(String schema) {
        String url = getUrlForSchema(schema);
        String username = getUsernameForSchema(schema);
        String password = getPasswordForSchema(schema);
        String driverClassName = getDriverClassNameForSchema(schema);

        return new DatabaseConfiguration(url, username, password, driverClassName);
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

    public void closeAllConnections() {
        dataSources.values().forEach(HikariDataSource::close);
        dataSources.clear();
    }
}
