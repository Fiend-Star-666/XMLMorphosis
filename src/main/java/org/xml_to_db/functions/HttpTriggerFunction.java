package org.xml_to_db.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml_to_db.config.ConfigLoader;
import org.xml_to_db.core.handlers.ErrorHandler;
import org.xml_to_db.core.processors.XMLProcessor;
import org.xml_to_db.core.processors.XMLProcessorFactory;
import org.xml_to_db.database.DatabaseConnection;
import org.xml_to_db.database.DatabaseConnectionFactory;
import org.xml_to_db.queue.QueueService;
import org.xml_to_db.queue.QueueServiceFactory;
import org.xml_to_db.storage.StorageService;
import org.xml_to_db.storage.StorageServiceFactory;
import org.xml_to_db.utils.XMLValidator;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class HttpTriggerFunction extends BaseFunction {
    private final ConfigLoader config = ConfigLoader.getInstance();
    private final QueueService queueService;
    private final StorageService storageService;

    public HttpTriggerFunction() {
        String queueType = config.getProperty("QUEUE_TYPE");
        String storageType = config.getProperty("STORAGE_TYPE");
        this.queueService = QueueServiceFactory.getQueueService(queueType);
        this.storageService = StorageServiceFactory.getStorageService(storageType);
    }

    @FunctionName("HttpTriggerFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        return execute(request, context, this::processRequest);
    }

    private HttpResponseMessage processRequest(HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
        log.info("Processing XML to DB Function");

        try {
            List<String> xmlFiles = storageService.listFiles("xml");
            List<String> xsdFiles = storageService.listFiles("xsd");

            for (String xmlFilePath : xmlFiles) {
                for (String xsdFilePath : xsdFiles) {
                    processXmlFile(xmlFilePath, xsdFilePath, context);
                }
            }

            return request.createResponseBuilder(HttpStatus.OK).body("Data processed successfully").build();
        } catch (Exception e) {
            ErrorHandler.handleException("Error in processRequest:", e);
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request: " + e.getMessage())
                    .build();
        }
    }

    private void processXmlFile(String xmlFilePath, String xsdFilePath, ExecutionContext context) {
        try {
            boolean isValid = XMLValidator.validateXMLSchema(xsdFilePath, xmlFilePath);
            if (!isValid) {
                log.warn("Invalid XML Schema for file: {}", xmlFilePath);
                sendToDeadLetterQueue(xmlFilePath, "Invalid XML Schema");
                return;
            }

            String xmlContent = storageService.readFileContent(xmlFilePath);
            Document document = parseXmlDocument(xmlContent);

            XMLProcessor<?> processor = XMLProcessorFactory.getProcessor(xmlFilePath, xsdFilePath);
            Object processedData = processor.process(document);

            try (DatabaseConnection connection = DatabaseConnectionFactory.getConnection(xmlFilePath, xsdFilePath)) {
                connection.save(processedData, getTableName(xmlFilePath));
            }

            log.info("Data processed successfully for file: {}", xmlFilePath);
        } catch (Exception e) {
            ErrorHandler.handleException("Error processing XML file: {}", e);
            sendToDeadLetterQueue(xmlFilePath, e.getMessage());
        }
    }

    private Document parseXmlDocument(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlContent)));
    }

    private void sendToDeadLetterQueue(String filePath, String errorMessage) {
        try {
            String message = "File: %s, Error: %s".formatted(filePath, errorMessage);
            queueService.sendToDeadLetterQueue(message);
        } catch (Exception e) {
            ErrorHandler.handleException("Failed to send message to dead-letter queue:", e);
        }
    }

    private String getTableName(String xmlFilePath) {
        // Extract the file name from the path
        String fileName = xmlFilePath.substring(xmlFilePath.lastIndexOf('/') + 1);
        // Remove the file extension
        fileName = fileName.replaceFirst("[.][^.]+$", "");
        // Convert to snake_case and prefix with "xml_" and suffix with "_data"
        return "xml_" + camelToSnakeCase(fileName) + "_data";
    }

    private String camelToSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
