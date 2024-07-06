package org.xmlToDb.core.parsers;

import org.xmlToDb.core.models.ParsedData;

import javax.xml.validation.Schema;

public interface XmlParser {
    ParsedData parse(String xmlContent, Schema schema) throws Exception;
}
