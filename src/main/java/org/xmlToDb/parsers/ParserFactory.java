package org.xmlToDb.parsers;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

public class ParserFactory {
    private ParserFactory() {
    }

    public static XmlParser getParser(String xmlContent) {
        if (xmlContent.contains("<complex>")) {
            return new DomParser();
        } else {
            return new SaxParser();
        }
    }

    public static Schema getSchema(String schemaPath) throws Exception {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return schemaFactory.newSchema(new File(schemaPath));
    }
}
