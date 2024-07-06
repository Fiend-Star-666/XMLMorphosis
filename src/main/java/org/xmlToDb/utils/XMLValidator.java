package org.xmlToDb.utils;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

public class XMLValidator {
    public static Boolean validateXMLSchema(String schemaPath, String xmlPath) {
        try {

            // 1. Lookup a factory for the W3C XML Schema language
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // 2. Compile the schema.
            File schemaLocation = new File(schemaPath);
            Schema schema = factory.newSchema(schemaLocation);

            // 3. Get a validator from the schema.
            Validator validator = schema.newValidator();

            // 4. Parse the document you want to check.
            Source source = new StreamSource(new File(xmlPath));

            // 5. Check the document
            validator.validate(source);
            System.out.println(xmlPath + " is valid.");
            return true;
        } catch (Exception ex) {
            // The document is not valid
            System.out.println(xmlPath + " is not valid because ");
            System.out.println(ex.getMessage());
        }
        return false;
    }
}
