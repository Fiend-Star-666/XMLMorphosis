package org.xml_to_db.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsedData {
    private String rootElementName;
    private final Map<String, String> fields;
    private final List<String> nestedValues;

    public ParsedData() {
        this.fields = new HashMap<>();
        this.nestedValues = new ArrayList<>();
    }

    public String getRootElementName() {
        return rootElementName;
    }

    public void setRootElementName(String rootElementName) {
        this.rootElementName = rootElementName;
    }

    public void addField(String key, String value) {
        fields.put(key, value);
    }

    public String getField(String key) {
        return fields.get(key);
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void addNestedValue(String value) {
        nestedValues.add(value);
    }

    public List<String> getNestedValues() {
        return nestedValues;
    }

    @Override
    public String toString() {
        return "ParsedData{"
                + "rootElementName='" + rootElementName + '\''
                + ", fields=" + fields
                + ", nestedValues=" + nestedValues
                + '}';
    }
}
