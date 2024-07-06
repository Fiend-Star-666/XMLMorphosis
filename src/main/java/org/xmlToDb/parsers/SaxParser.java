package org.xmlToDb.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlToDb.models.ParsedData;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import java.io.ByteArrayInputStream;
import java.util.Stack;

public class SaxParser extends DefaultHandler implements XmlParser {
    private ParsedData data;
    private StringBuilder currentValue;
    private Stack<String> elementStack;

    @Override
    public ParsedData parse(String xmlContent, Schema schema) throws Exception {
        data = new ParsedData();
        currentValue = new StringBuilder();
        elementStack = new Stack<>();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setSchema(schema);
        factory.setNamespaceAware(true);
        factory.setValidating(true);

        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(new ByteArrayInputStream(xmlContent.getBytes()), this);

        return data;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        elementStack.push(qName);
        currentValue.setLength(0);

        if (elementStack.size() == 1) {
            data.setRootElementName(qName);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String currentElement = elementStack.pop();

        if (currentElement.equals("nested")) {
            data.addNestedValue(currentValue.toString().trim());
        } else {
            data.addField(currentElement, currentValue.toString().trim());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentValue.append(ch, start, length);
    }
}