package org.xml_to_db.database.strategy;

public class DatabaseStrategyFactory {
    private DatabaseStrategyFactory() {
    }

    public static DatabaseStrategy getDatabaseStrategy(String dbType, String url, String username, String password) {
        if ("sqlserver".equalsIgnoreCase(dbType)) {
            return new SqlServerStrategy(url, username, password);
            // Add cases for other database types
        }
        throw new IllegalArgumentException("Unknown database type: " + dbType);
    }
}
