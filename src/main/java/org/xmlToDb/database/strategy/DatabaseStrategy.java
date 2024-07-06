package org.xmlToDb.database.strategy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.xmlToDb.core.dbModels.DataRetrievalLog;

public interface DatabaseStrategy {
    JdbcTemplate getJdbcTemplate();

    void save(DataRetrievalLog dataRetrievalLog);
}
