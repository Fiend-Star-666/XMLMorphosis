package org.xml_to_db.core.processors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultXMLProcessor implements XMLProcessor {

    @Override
    public Object process(Document document) {
        Element rootElement = document.getDocumentElement();
        return processElement(rootElement);
    }

    private Object processElement(Element element) {
        Map<String, Object> result = new HashMap<>();

        // Process attributes
        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            Node attr = element.getAttributes().item(i);
            result.put("@" + attr.getNodeName(), attr.getNodeValue());
        }

        // Process child elements
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = node.getNodeName();
                Object childValue = processElement((Element) node);

                if (result.containsKey(nodeName)) {
                    // If this node name already exists, convert to a list or add to existing list
                    Object existing = result.get(nodeName);
                    if (existing instanceof List list) {
                        list.add(childValue);
                    } else {
                        List<Object> list = new ArrayList<>();
                        list.add(existing);
                        list.add(childValue);
                        result.put(nodeName, list);
                    }
                } else {
                    result.put(nodeName, childValue);
                }
            } else if (node.getNodeType() == Node.TEXT_NODE) {
                String text = node.getTextContent().trim();
                if (!text.isEmpty()) {
                    result.put("text", text);
                }
            }
        }

        // If the element has only text content, return the text directly
        if (result.size() == 1 && result.containsKey("text")) {
            return result.get("text");
        }

        return result;
    }
}
