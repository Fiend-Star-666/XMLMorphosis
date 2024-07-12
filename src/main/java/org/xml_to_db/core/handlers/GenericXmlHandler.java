package org.xml_to_db.core.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml_to_db.core.models.ParsedData;
import org.xml_to_db.core.parsers.ParserFactory;
import org.xml_to_db.core.parsers.XmlParser;
import org.xml_to_db.database.DatabaseConnection;
import org.xml_to_db.database.DatabaseConnectionManager;
import org.xml_to_db.utils.SchemaValidator;

import javax.xml.validation.Schema;
import java.sql.SQLException;

public class GenericXmlHandler implements XmlHandler {
    private static final Logger logger = LoggerFactory.getLogger(GenericXmlHandler.class);
    private final DatabaseConnectionManager dbManager;

    public GenericXmlHandler() {
        this.dbManager = DatabaseConnectionManager.getInstance();
    }

    @Override
    public void handle(String xmlContent, String schemaPath) {
        try {
            Schema schema = ParserFactory.getSchema(schemaPath);

            if (SchemaValidator.validateXMLSchema(schemaPath, xmlContent)) {
                XmlParser parser = ParserFactory.getParser(xmlContent);
                ParsedData data = parser.parse(xmlContent, schema);

                // Save parsed data to the database
                String tableName = determineTableName(data);
                try (DatabaseConnection dbConnection = dbManager.getConnection(schemaPath)) {
                    dbConnection.save(data, tableName);
                }
                logger.info("Successfully processed and saved XML data to table: {}", tableName);
            } else {
                handleSchemaValidationFailure(xmlContent, schemaPath);
            }
        } catch (SQLException e) {
            ErrorHandler.handleException("Failed to save data to database", e);
            handleDatabaseError(xmlContent, schemaPath, e);
        } catch (Exception e) {
            ErrorHandler.handleException("Error processing XML content", e);
            handleProcessingError(xmlContent, schemaPath, e);
        }
    }

    private String determineTableName(ParsedData data) {
        // Logic to determine table name based on parsed data
        // This is a placeholder implementation
        return "xml_" + data.getRootElementName().toLowerCase() + "_data";
    }

    private void handleSchemaValidationFailure(String xmlContent, String schemaPath) {
        logger.error("XML content failed schema validation. Schema: {}", schemaPath);
        // Implement your error handling logic here, for example:
        // 1. Log the invalid XML content for debugging
        logger.debug("Invalid XML content: {}", xmlContent);
        // 2. Notify administrators
        notifyAdministrators("Schema validation failure", schemaPath);
        // 3. Move the invalid XML to a separate storage for later analysis
        moveToErrorStorage(xmlContent, "schema_validation_failure");
        // 4. Implement retry logic with a different schema if applicable
        // retryWithDifferentSchema(xmlContent);
    }

    private void handleDatabaseError(String xmlContent, String schemaPath, SQLException e) {
        // Implement your database error handling logic here, for example:
        // 1. Implement retry logic
        // retryDatabaseOperation(xmlContent, schemaPath);
        // 2. Move the XML to a retry queue
        moveToRetryQueue(xmlContent, schemaPath);
        // 3. Notify administrators
        notifyAdministrators("Database error", e.getMessage());
    }

    private void handleProcessingError(String xmlContent, String schemaPath, Exception e) {
        // Implement your processing error handling logic here, for example:
        // 1. Log detailed error information
        logger.error("Processing error details: ", e);
        // 2. Move the XML to an error storage
        moveToErrorStorage(xmlContent, "processing_error");
        // 3. Notify administrators
        notifyAdministrators("XML processing error", e.getMessage());
    }

    // Placeholder methods for error handling actions
    private void notifyAdministrators(String subject, String message) {
        // Implement notification logic (e.g., send email, create alert)
        logger.info("Admin notification: {} - {}", subject, message);
    }

    private void moveToErrorStorage(String xmlContent, String errorType) {
        // Implement logic to move XML to error storage
        logger.info("Moving XML to error storage. Error type: {}", errorType);
    }

    private void moveToRetryQueue(String xmlContent, String schemaPath) {
        // Implement logic to move XML to retry queue
        logger.info("Moving XML to retry queue. Schema: {}", schemaPath);
    }
}
