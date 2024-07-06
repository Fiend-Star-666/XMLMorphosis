package org.xmlToDb.handlers;

import org.xmlToDb.models.ParsedData;
import org.xmlToDb.parsers.ParserFactory;
import org.xmlToDb.strategies.XmlHandler;
import org.xmlToDb.strategies.XmlParser;
import org.xmlToDb.utils.DatabaseConnectionManager;
import org.xmlToDb.utils.SchemaValidator;

public class GenericXmlHandler implements XmlHandler {
    private final DatabaseConnectionManager dbManager;

    public GenericXmlHandler() {
        this.dbManager = DatabaseConnectionManager.getInstance();
    }

    @Override
    public void handle(String xmlContent, String schemaPath) {
        if (SchemaValidator.validateXMLSchema(schemaPath, xmlContent)) {
            XmlParser parser = ParserFactory.getParser(xmlContent);
            ParsedData data = parser.parse(xmlContent, schemaPath);
            // Save parsed data to the database
            dbManager.getConnection(data.getSchema()).saveData(data);
        } else {
            // Handle schema validation failure
        }
    }
}
