package org.xmlToDb.strategies;

import org.xmlToDb.models.ParsedData;

public interface XmlParser {
    ParsedData parse(String xmlContent, String schemaPath);
}
