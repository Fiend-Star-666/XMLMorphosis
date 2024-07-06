package org.xmlToDb.strategy;

import org.springframework.jdbc.core.JdbcTemplate;

public interface DatabaseStrategy {
    JdbcTemplate getJdbcTemplate();
}

