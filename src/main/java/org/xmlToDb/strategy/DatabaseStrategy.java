package org.xmlToDb.strategy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.xmlToDb.dbModels.DataRetrievalLog;

public interface DatabaseStrategy {
    JdbcTemplate getJdbcTemplate();

    void save(DataRetrievalLog dataRetrievalLog);
}

