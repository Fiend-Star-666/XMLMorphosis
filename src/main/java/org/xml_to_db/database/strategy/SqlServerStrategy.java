package org.xml_to_db.database.strategy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.xml_to_db.core.dbModels.DataRetrievalLog;
import org.xml_to_db.core.dbModels.DatabaseModelObject;

public class SqlServerStrategy implements DatabaseStrategy {
    private final JdbcTemplate jdbcTemplate;

    public SqlServerStrategy(String url, String username, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public void save(DatabaseModelObject databaseModelObject) {

    }
}
