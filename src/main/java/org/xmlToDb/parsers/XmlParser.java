package org.xmlToDb.parsers;

import org.xmlToDb.models.ParsedData;

import javax.xml.validation.Schema;

public interface XmlParser {
    ParsedData parse(String xmlContent, Schema schema) throws Exception;
}