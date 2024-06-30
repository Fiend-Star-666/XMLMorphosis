package org.xmlToDb.parsers;

import org.w3c.dom.Document;
import org.xmlToDb.models.ParsedData;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DomParser implements XmlParser {
    @Override
    public ParsedData parse(String xmlContent, String schemaPath) {
        ParsedData data = new ParsedData();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new java.io.ByteArrayInputStream(xmlContent.getBytes()));

            // Populate data from XML
            // Example:
            // data.setValue(document.getElementsByTagName("someTag").item(0).getTextContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
