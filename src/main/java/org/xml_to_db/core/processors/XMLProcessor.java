package org.xml_to_db.core.processors;

import org.w3c.dom.Document;

public interface XMLProcessor<T> {
    T process(Document document);
}
