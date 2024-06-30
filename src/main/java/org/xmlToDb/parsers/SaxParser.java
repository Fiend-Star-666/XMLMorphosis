package org.xmlToDb.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlToDb.models.ParsedData;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;

public class SaxParser extends DefaultHandler implements XmlParser {
    private ParsedData data;

    @Override
    public ParsedData parse(String xmlContent, String schemaPath) {
        data = new ParsedData();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new ByteArrayInputStream(xmlContent.getBytes()), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // Override necessary methods to populate data
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // Example:
        // if (qName.equalsIgnoreCase("someTag")) {
        //     data.setValue(attributes.getValue("attribute"));
        // }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // Populate data
    }
}