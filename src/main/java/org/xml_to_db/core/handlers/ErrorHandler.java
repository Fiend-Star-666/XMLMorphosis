package org.xml_to_db.core.handlers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorHandler {
    public static void handleException(String operation, Exception e) {
        log.error("Error during {}: {}", operation, e.getMessage(), e);
        throw new RuntimeException("Error during " + operation, e);
        // Implement additional error handling logic (e.g., sending alerts, writing to error log)
    }

    public static void handleException(String operation, Exception e, Boolean showRealException) throws Exception {
        log.error("Error during {}: {}", operation, e.getMessage(), e);
        if(Boolean.TRUE.equals(showRealException))
            throw e;
        // Implement additional error handling logic (e.g., sending alerts, writing to error log)
    }

}
