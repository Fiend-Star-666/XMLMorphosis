package org.xmlToDb.utils;

import org.xmlToDb.models.ParsedData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private Connection connection;

    public DatabaseConnection(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveData(ParsedData data) {
        // Implement logic to save data to the database
    }

    public Connection getConnection() {
        return connection;
    }
}
