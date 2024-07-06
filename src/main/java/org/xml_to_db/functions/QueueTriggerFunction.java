package org.xml_to_db.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import org.xml_to_db.services.XmlProcessingService;

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
