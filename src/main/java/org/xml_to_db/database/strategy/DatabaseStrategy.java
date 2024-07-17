package org.xml_to_db.database.strategy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.xml_to_db.core.dbModels.DatabaseModelObject;

public interface DatabaseStrategy {
    JdbcTemplate getJdbcTemplate();

    void save(DatabaseModelObject databaseModelObject);
}
