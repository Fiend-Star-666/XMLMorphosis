package org.xmlToDb.functions;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.jetbrains.annotations.NotNull;
import org.xmlToDb.config.ConfigLoader;
import org.xmlToDb.dbModels.DataRetrievalLog;
import org.xmlToDb.factory.ServiceFactory;
import org.xmlToDb.queue.QueueService;
import org.xmlToDb.storage.StorageService;
import org.xmlToDb.strategy.DatabaseStrategy;
import org.xmlToDb.utils.BaseFunction;
import org.xmlToDb.utils.XMLValidator;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class HttpTriggerFunction extends BaseFunction {

    @FunctionName("HttpTriggerFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        return execute(request, context, this::processRequest);
    }

    private HttpResponseMessage processRequest(HttpRequestMessage<Optional<String>> request, @NotNull ExecutionContext context) {
        context.getLogger().info("Processing XML to DB Function");

        try {
            String cloudProvider = ConfigLoader.getProperty("CLOUD_PROVIDER");
            String dbType = ConfigLoader.getProperty("DB_TYPE");
            String dbUrl = ConfigLoader.getProperty("DB_URL");
            String dbUsername = ConfigLoader.getProperty("DB_USERNAME");
            String dbPassword = ConfigLoader.getProperty("DB_PASSWORD");

            QueueService queueService = ServiceFactory.getQueueService(cloudProvider);
            StorageService storageService = ServiceFactory.getStorageService(cloudProvider);
            DatabaseStrategy databaseStrategy = ServiceFactory.getDatabaseStrategy(dbType, dbUrl, dbUsername, dbPassword);

            List<String> xmlFiles = fetchFileList(storageService);
            List<String> xsdFiles = fetchXsdList(storageService);

            for (String xmlFilePath : xmlFiles) {
                for (String xsdFilePath : xsdFiles) {
                    processXmlFile(xmlFilePath, xsdFilePath, databaseStrategy, queueService, storageService, context);
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

    private void processXmlFile(String xmlFilePath, String xsdFilePath, DatabaseStrategy databaseStrategy,
                                QueueService queueService, StorageService storageService, ExecutionContext context) {
        try {
            boolean isValid = XMLValidator.validateXMLSchema(xsdFilePath, xmlFilePath);
            if (!isValid) {
                context.getLogger().warning("Invalid XML Schema for file: " + xmlFilePath);
                return;
            }

            String xmlContent = storageService.readFileContent(xmlFilePath);

            XmlMapper xmlMapper = new XmlMapper();
            DataRetrievalLog dataRetrievalLog = xmlMapper.readValue(xmlContent, DataRetrievalLog.class);

            databaseStrategy.save(dataRetrievalLog);

            context.getLogger().info("Data processed successfully for file: " + xmlFilePath);
        } catch (Exception e) {
            context.getLogger().severe("Error processing XML file: " + e.getMessage());
            queueService.sendToDeadLetterQueue(e.getMessage());
        }
    }

    private List<String> fetchFileList(StorageService storageService) throws IOException {
        return storageService.listFiles("xml");
    }

    private List<String> fetchXsdList(StorageService storageService) throws IOException {
        return storageService.listFiles("xsd");
    }
}