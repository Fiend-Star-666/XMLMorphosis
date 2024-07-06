package org.xmlToDb.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import org.xmlToDb.services.XmlProcessingService;

public class QueueTriggerFunction {
    @FunctionName("QueueTriggerFunction")
    public void processQueueMessage(
            @QueueTrigger(name = "message", queueName = "incoming-xml-files", connection = "AzureWebJobsStorage") String xmlContent,
            final ExecutionContext context) {

        XmlProcessingService service = new XmlProcessingService();
        service.processXml(xmlContent);

        // Archive the file (example: uploading to Azure Blob Storage)
        // BlobClient blobClient = new BlobClient("xml-archive");
        // blobClient.upload(xmlContent);
    }
}
