package org.xml_to_db.utils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.StringReader;

@Slf4j
public class XMLValidator {

    private XMLValidator() {
        // Private constructor to prevent instantiation
    }

    public static boolean validateXMLSchema(String xsdPath, String xmlPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlPath));
            return true;
        } catch (Exception e) {
            log.error("XML Validation Error: {}", e.getMessage(), e);
            return false;
        }
    }

    public static <T> T parseXml(String xmlContent, Class<T> clazz) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(xmlContent);
        Object result = unmarshaller.unmarshal(reader);

        if (clazz.isInstance(result)) {
            return clazz.cast(result);
        } else if (result instanceof JAXBElement<?> jaxbElement) {
            Object value = jaxbElement.getValue();
            if (clazz.isInstance(value)) {
                return clazz.cast(value);
            }
        }

        throw new ClassCastException("Unmarshalled object is not of type " + clazz.getName());
    }
}
