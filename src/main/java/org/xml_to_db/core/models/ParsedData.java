package org.xml_to_db.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@Getter
public class ParsedData {
    private final Map<String, String> fields;
    private final List<String> nestedValues;
    private String rootElementName;

    public ParsedData() {
        this.fields = new HashMap<>();
        this.nestedValues = new ArrayList<>();
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

    public void addNestedValue(String value) {
        nestedValues.add(value);
    }

}
