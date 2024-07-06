package org.xmlToDb.parsers;

import org.xmlToDb.models.ParsedData;

public interface XmlParser {
    ParsedData parse(String xmlContent, String schemaPath);
}
