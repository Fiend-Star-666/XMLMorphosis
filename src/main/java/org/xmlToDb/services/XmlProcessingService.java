package org.xmlToDb.services;

import org.xmlToDb.handlers.GenericXmlHandler;
import org.xmlToDb.utils.SchemaValidator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class XmlProcessingService {
    private final Map<String, String> schemaMap;
    private final GenericXmlHandler handler;

    public XmlProcessingService() {
        this.schemaMap = new HashMap<>();
        this.handler = new GenericXmlHandler();

        // Load available schemas
        File schemaDir = new File("src/main/resources/schemas");
        for (File schemaFile : schemaDir.listFiles()) {
            schemaMap.put(schemaFile.getName(), schemaFile.getPath());
        }
    }

    public void processXml(String xmlContent) {
        for (Map.Entry<String, String> entry : schemaMap.entrySet()) {
            if (SchemaValidator.validateXMLSchema(entry.getValue(), xmlContent)) {
                handler.handle(xmlContent, entry.getValue());
                break;
            }
        }
    }
}
