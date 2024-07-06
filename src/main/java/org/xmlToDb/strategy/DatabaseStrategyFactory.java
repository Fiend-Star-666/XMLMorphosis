package org.xmlToDb.strategy;

public class DatabaseStrategyFactory {
    public static DatabaseStrategy getDatabaseStrategy(String dbType, String url, String username, String password) {
        switch (dbType.toLowerCase()) {
            case "sqlserver":
                return new SqlServerStrategy(url, username, password);
            // Add cases for other database types
            default:
                throw new IllegalArgumentException("Unknown database type: " + dbType);
        }
    }

    private DatabaseStrategyFactory() {
    }
}
