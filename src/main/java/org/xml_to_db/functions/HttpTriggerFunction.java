package org.xml_to_db.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml_to_db.config.ConfigLoader;
import org.xml_to_db.core.processors.XMLProcessor;
import org.xml_to_db.core.processors.XMLProcessorFactory;
import org.xml_to_db.queue.QueueService;
import org.xml_to_db.queue.QueueServiceFactory;
import org.xml_to_db.storage.StorageService;
import org.xml_to_db.storage.StorageServiceFactory;
import org.xml_to_db.database.DatabaseConnection;
import org.xml_to_db.database.DatabaseConnectionFactory;
import org.xml_to_db.utils.XMLValidator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
public class HttpTriggerFunction extends BaseFunction {

    private final ConfigLoader config = ConfigLoader.getInstance();
    private final QueueService queueService;

    public HttpTriggerFunction() {
        String queueType = config.getProperty("QUEUE_TYPE");
        this.queueService = QueueServiceFactory.getQueueService();
    }

    @FunctionName("HttpTriggerFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        return execute(request, context, this::processRequest);
    }

    private HttpResponseMessage processRequest(HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
        context.getLogger().info("Processing XML to DB Function");

        try {
            String storageType = config.getProperty("STORAGE_TYPE");
            StorageService storageService = StorageServiceFactory.getStorageService(storageType);

            List<String> xmlFiles = storageService.listFiles("xml");
            List<String> xsdFiles = storageService.listFiles("xsd");

            for (String xmlFilePath : xmlFiles) {
                for (String xsdFilePath : xsdFiles) {
                    processXmlFile(xmlFilePath, xsdFilePath, storageService, context);
                }
            }

            return request.createResponseBuilder(HttpStatus.OK).body("Data processed successfully").build();
        } catch (Exception e) {
            context.getLogger().severe("Error in processRequest: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request: " + e.getMessage())
                    .build();
        }
    }

    private void processXmlFile(String xmlFilePath, String xsdFilePath, StorageService storageService, ExecutionContext context) {
        try {
            boolean isValid = XMLValidator.validateXMLSchema(xsdFilePath, xmlFilePath);
            if (!isValid) {
                context.getLogger().warning("Invalid XML Schema for file: " + xmlFilePath);
                sendToDeadLetterQueue(xmlFilePath, "Invalid XML Schema");
                return;
            }

            String xmlContent = storageService.readFileContent(xmlFilePath);

            // Parse XML to DOM Document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));

            // Process XML
            XMLProcessor processor = XMLProcessorFactory.getProcessor(xmlFilePath, xsdFilePath);
            Object processedData = processor.process(document);

            // Save to database
            try (DatabaseConnection connection = DatabaseConnectionFactory.getConnection(xmlFilePath, xsdFilePath)) {
                connection.save(processedData, getTableName(xmlFilePath));
            }

            context.getLogger().info("Data processed successfully for file: " + xmlFilePath);
        } catch (Exception e) {
            context.getLogger().severe("Error processing XML file: " + e.getMessage());
            sendToDeadLetterQueue(xmlFilePath, e.getMessage());
            // Implement retry mechanism here if needed
            // For example, you could add the file to a retry queue with a delay
            // retryQueue.addWithDelay(xmlFilePath, 5000); // 5 seconds delay
        }
    }

    private void sendToDeadLetterQueue(String filePath, String errorMessage) {
        try {
            String message = "File: %s, Error: %s".formatted(filePath, errorMessage);
            queueService.sendToDeadLetterQueue(message);
        } catch (Exception e) {
            // Log the error, but don't throw it to avoid disrupting the main process
            System.err.println("Failed to send message to dead-letter queue: " + e.getMessage());
        }
    }

    private String getTableName(String xmlFilePath) {
        // Extract the file name from the path
        Path path = Paths.get(xmlFilePath);
        String fileName = path.getFileName().toString();

        // Remove the file extension
        fileName = fileName.replaceFirst("[.][^.]+$", "");

        // Convert to snake_case
        String snakeCase = camelToSnakeCase(fileName);

        // Prefix with "xml_" and suffix with "_data"
        return "xml_" + snakeCase + "_data";
    }

    private String camelToSnakeCase(String str) {
        // Regular expression to convert camelCase to snake_case
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        str = str.replaceAll(regex, replacement).toLowerCase();

        // Replace any non-alphanumeric characters with underscore
        return str.replaceAll("[^a-zA-Z0-9]", "_");
    }
}
