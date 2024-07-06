package org.xmlToDb.functions;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.xmlToDb.config.ConfigLoader;
import org.xmlToDb.dbModels.DataRetrievalLog;
import org.xmlToDb.factory.ServiceFactory;
import org.xmlToDb.queue.QueueService;
import org.xmlToDb.storage.StorageService;
import org.xmlToDb.strategy.DatabaseStrategy;
import org.xmlToDb.utils.BaseFunction;
import org.xmlToDb.utils.XMLValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class HttpTriggerFunction extends BaseFunction {

    @FunctionName("HttpTriggerFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        return execute(request, context, this::processRequest);
    }

    private HttpResponseMessage processRequest(HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
        context.getLogger().info("Processing XML to DB Function");

        // Read cloud provider and DB connection details from environment variables
        String cloudProvider = ConfigLoader.getProperty("CLOUD_PROVIDER");
        String dbType = ConfigLoader.getProperty("DB_TYPE");
        String dbUrl = ConfigLoader.getProperty("DB_URL");
        String dbUsername = ConfigLoader.getProperty("DB_USERNAME");
        String dbPassword = ConfigLoader.getProperty("DB_PASSWORD");

        QueueService queueService = ServiceFactory.getQueueService(cloudProvider);
        StorageService storageService = ServiceFactory.getStorageService(cloudProvider);
        DatabaseStrategy databaseStrategy = ServiceFactory.getDatabaseStrategy(dbType, dbUrl, dbUsername, dbPassword);

        // Fetch file list
        List<String> xmlFiles = fetchFileList();
        List<String> xsdFiles = fetchXsdList();

        for (String xmlFilePath : xmlFiles) {
            for (String xsdFilePath : xsdFiles) {
                // Validate XML against XSD schema
                boolean isValid = XMLValidator.validateXMLSchema(xsdFilePath, xmlFilePath);
                if (!isValid) {
                    context.getLogger().warning("Invalid XML Schema for file: " + xmlFilePath);
                    continue;
                }

                try {
                    // Read XML file
                    String xmlContent = new String(Files.readAllBytes(Paths.get(xmlFilePath)));

                    // Parse XML to DataRetrievalLog object
                    XmlMapper xmlMapper = new XmlMapper();
                    DataRetrievalLog dataRetrievalLog = xmlMapper.readValue(xmlContent, DataRetrievalLog.class);

                    // Process the data (e.g., store in database)
                    // storageService.storeFile(xmlFilePath);

                    context.getLogger().info("Data processed successfully for file: " + xmlFilePath);
                } catch (IOException e) {
                    context.getLogger().severe("Error reading XML file: " + e.getMessage());
                    queueService.sendToDeadLetterQueue(e.getMessage());
                }
            }
        }

        return request.createResponseBuilder(HttpStatus.OK).body("Data processed successfully").build();
    }

    private List<String> fetchFileList() {
        // Implement logic to fetch the list of XML files
        // This can be from a Blob Storage, SFTP, etc.
        return List.of("/path/to/your/file1.xml", "/path/to/your/file2.xml"); // Example paths
    }

    private List<String> fetchXsdList() {
        // Implement logic to fetch the list of XSD files
        // This can be from a Blob Storage, SFTP, etc.
        return List.of("/path/to/your/schema1.xsd", "/path/to/your/schema2.xsd"); // Example paths
    }

}
