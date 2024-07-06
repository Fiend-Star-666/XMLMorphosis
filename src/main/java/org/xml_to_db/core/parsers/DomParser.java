package org.xml_to_db.core.parsers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml_to_db.core.models.ParsedData;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import java.io.ByteArrayInputStream;

public class DomParser implements XmlParser {
    @Override
    public ParsedData parse(String xmlContent, Schema schema) throws Exception {
        ParsedData data = new ParsedData();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setSchema(schema);
        factory.setNamespaceAware(true);
        factory.setValidating(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));

        Element root = document.getDocumentElement();
        data.setRootElementName(root.getTagName());

        // Parse specific elements
        parseElement(root, "id", data);
        parseElement(root, "name", data);
        parseElement(root, "description", data);

        // Parse nested elements
        NodeList nestedList = root.getElementsByTagName("nested");
        for (int i = 0; i < nestedList.getLength(); i++) {
            Element nested = (Element) nestedList.item(i);
            String nestedValue = nested.getTextContent();
            data.addNestedValue(nestedValue);
        }

        return data;
    }

    private void parseElement(Element root, String tagName, ParsedData data) {
        NodeList nodeList = root.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            String value = nodeList.item(0).getTextContent();
            data.addField(tagName, value);
        }
    }
}
