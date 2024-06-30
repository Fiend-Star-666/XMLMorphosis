package org.xmlToDb.utils;

import java.util.HashMap;
import java.util.Map;

public class DatabaseConnectionManager {
    private static DatabaseConnectionManager instance;
    private Map<String, DatabaseConnection> connectionMap;

    private DatabaseConnectionManager() {
        connectionMap = new HashMap<>();
    }

    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }

    public DatabaseConnection getConnection(String schema) {
        return connectionMap.computeIfAbsent(schema, k -> new DatabaseConnection("jdbc:" + schema + "-azure-sql-url", "username", "password"));
    }
}

