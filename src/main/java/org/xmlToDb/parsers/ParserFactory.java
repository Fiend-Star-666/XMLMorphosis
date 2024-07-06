package org.xmlToDb.parsers;

public class ParserFactory {
    private ParserFactory() {
    }

    public static XmlParser getParser(String xmlContent) {
        if (xmlContent.contains("<complex>")) {
            return new DomParser();
        } else {
            return new SaxParser();
        }
    }
}
