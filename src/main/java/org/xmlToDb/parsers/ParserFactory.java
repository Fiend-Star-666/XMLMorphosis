package org.xmlToDb.parsers;

import org.xmlToDb.strategies.XmlParser;

public class ParserFactory {
    public static XmlParser getParser(String xmlContent) {
        if (xmlContent.contains("<complex>")) {
            return new DomParser();
        } else {
            return new SaxParser();
        }
    }

    private ParserFactory() {
    }
}
