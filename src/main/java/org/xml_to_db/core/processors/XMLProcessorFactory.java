package org.xml_to_db.core.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml_to_db.config.ConfigLoader;
import org.xml_to_db.core.handlers.ErrorHandler;

import java.util.HashMap;
import java.util.Map;

public class XMLProcessorFactory {
    private static final Logger logger = LoggerFactory.getLogger(XMLProcessorFactory.class);
    private static final Map<String, XMLProcessor<?>> processors = new HashMap<>();
    private static final DefaultXMLProcessor defaultProcessor = new DefaultXMLProcessor();

    private static final ConfigLoader config = ConfigLoader.getInstance();

    static {
        loadProcessors();
    }

    public static XMLProcessor<?> getProcessor(String xmlPath, String xsdPath) {
        String key = determineProcessorKey(xmlPath, xsdPath);
        return processors.getOrDefault(key, defaultProcessor);
    }

    private static String determineProcessorKey(String xmlPath, String xsdPath) {
        // This method should implement the logic to determine the appropriate processor
        // based on the XML and XSD files. For now, we'll use a simple approach based on file names.
        String xmlFileName = xmlPath.substring(xmlPath.lastIndexOf('/') + 1);
        String xsdFileName = xsdPath.substring(xsdPath.lastIndexOf('/') + 1);
        return xmlFileName + ":" + xsdFileName;
    }

    private static void loadProcessors() {
        String[] processorClasses = config.getProperty("XML_PROCESSOR_CLASSES").split(",");
        for (String processorClassName : processorClasses) {
            try {
                Class<?> clazz = Class.forName(processorClassName.trim());
                if (XMLProcessor.class.isAssignableFrom(clazz)) {
                    XMLProcessor<?> processor = (XMLProcessor<?>) clazz.getDeclaredConstructor().newInstance();
                    processors.put(clazz.getSimpleName(), processor);
                    logger.info("Loaded XML processor: {}", processorClassName);
                } else {
                    logger.warn("Class {} does not implement XMLProcessor interface", processorClassName);
                }
            } catch (Exception e) {
                ErrorHandler.handleException("Error loading XML processor:" + processorClassName, e);
            }
        }
        if (processors.isEmpty()) {
            logger.warn("No XML processor classes configured");
        }
    }
}
