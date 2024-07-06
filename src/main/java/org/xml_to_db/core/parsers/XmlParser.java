package org.xml_to_db.core.parsers;

import org.xml_to_db.core.models.ParsedData;

import javax.xml.validation.Schema;

public interface XmlParser {
    ParsedData parse(String xmlContent, Schema schema) throws Exception;
}
