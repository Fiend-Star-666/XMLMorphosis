package org.xml_to_db.utils;

import org.xml_to_db.core.handlers.ErrorHandler;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;

public class SchemaValidator {
    private SchemaValidator() {
    }

    public static boolean validateXMLSchema(String schemaPath, String xmlContent) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(schemaPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlContent)));
            return true;
        } catch (Exception e) {
            ErrorHandler.handleException("Exception while validating Schema", e);
            return false;
        }
    }
}
