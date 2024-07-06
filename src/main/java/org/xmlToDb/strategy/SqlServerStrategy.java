package org.xmlToDb.strategy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.xmlToDb.dbModels.DataRetrievalLog;

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
    public void save(DataRetrievalLog dataRetrievalLog) {

    }
}
