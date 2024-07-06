package org.xmlToDb.database;

import org.xmlToDb.core.models.ParsedData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseConnection implements AutoCloseable {
    private final Connection connection;

    public DatabaseConnection(DatabaseConfiguration config) throws SQLException, ClassNotFoundException {
        Class.forName(config.getDriverClassName());
        this.connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
    }

    public void save(Object data, String tableName) throws SQLException {
        if (!(data instanceof ParsedData)) {
            throw new IllegalArgumentException("Data must be of type ParsedData");
        }
        ParsedData parsedData = (ParsedData) data;
        Map<String, String> fields = parsedData.getFields();

        String sql = generateInsertSQL(fields, tableName);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setStatementParameters(statement, fields);
            statement.executeUpdate();
        }
    }

    private String generateInsertSQL(Map<String, String> fields, String tableName) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (String fieldName : fields.keySet()) {
            if (columns.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(fieldName);
            values.append("?");
        }

        return "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
    }

    private void setStatementParameters(PreparedStatement statement, Map<String, String> fields) throws SQLException {
        int parameterIndex = 1;
        for (Object value : fields.values()) {
            statement.setObject(parameterIndex++, value);
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void saveData(ParsedData data, String tableName) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder values = new StringBuilder(") VALUES (");

        List<Object> parameterValues = new ArrayList<>();

        for (Map.Entry<String, String> entry : data.getFields().entrySet()) {
            if (!parameterValues.isEmpty()) {
                sql.append(", ");
                values.append(", ");
            }
            sql.append(entry.getKey());
            values.append("?");
            parameterValues.add(entry.getValue());
        }

        sql.append(values).append(")");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameterValues.size(); i++) {
                stmt.setObject(i + 1, parameterValues.get(i));
            }
            stmt.executeUpdate();
        }
    }
}
