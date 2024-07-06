package org.xmlToDb.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlToDb.config.ConfigLoader;

import java.util.HashMap;
import java.util.Map;

public class XMLProcessorFactory {
    private static final Logger logger = LoggerFactory.getLogger(XMLProcessorFactory.class);
    private static final Map<String, XMLProcessor> processors = new HashMap<>();
    private static final DefaultXMLProcessor defaultProcessor = new DefaultXMLProcessor();

    ConfigLoader config = ConfigLoader.getInstance();

    public static XMLProcessor getProcessor(String xmlPath, String xsdPath) {
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

    private void loadProcessors() {
        String processorClassesString = config.getProperty("XML_PROCESSOR_CLASSES");
        if (processorClassesString != null && !processorClassesString.isEmpty()) {
            String[] processorClasses = processorClassesString.split(",");
            for (String processorClass : processorClasses) {
                try {
                    Class<?> clazz = Class.forName(processorClass.trim());
                    if (XMLProcessor.class.isAssignableFrom(clazz)) {
                        XMLProcessor processor = (XMLProcessor) clazz.getDeclaredConstructor().newInstance();
                        processors.put(processorClass, processor);
                        logger.info("Loaded XML processor: {}", processorClass);
                    } else {
                        logger.warn("Class {} does not implement XMLProcessor interface", processorClass);
                    }
                } catch (Exception e) {
                    logger.error("Error loading XML processor: " + processorClass, e);
                }
            }
        } else {
            logger.warn("No XML processor classes configured");
        }
    }
}