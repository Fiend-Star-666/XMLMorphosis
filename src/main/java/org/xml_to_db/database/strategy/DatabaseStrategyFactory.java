package org.xml_to_db.database.strategy;

public class DatabaseStrategyFactory {
    public static DatabaseStrategy getDatabaseStrategy(String dbType, String url, String username, String password) {
        if (dbType.equalsIgnoreCase("sqlserver")) {
            return new SqlServerStrategy(url, username, password);
            // Add cases for other database types
        }
        throw new IllegalArgumentException("Unknown database type: " + dbType);
    }

    private DatabaseStrategyFactory() {
    }
}
