package org.xmlToDb.queue;

import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import org.xmlToDb.utils.AzureQueueHelper;

public class AzureQueueService implements QueueService {

    private final CloudQueue queue;
    private final CloudQueue deadLetterQueue;

    public AzureQueueService() throws Exception {
        // Initialize Azure queue and DLQ from environment variables
        String queueName = System.getenv("AZURE_QUEUE_NAME");
        String dlqName = System.getenv("AZURE_DLQ_NAME");
        this.queue = AzureQueueHelper.getQueueReference(queueName);
        this.deadLetterQueue = AzureQueueHelper.getQueueReference(dlqName);
    }

    @Override
    public void processMessage(String message) {
        try {
            // Process the message
            // If processing fails, send to DLQ
        } catch (Exception e) {
            sendToDeadLetterQueue(message);
        }
    }

    @Override
    public void sendToDeadLetterQueue(String message) {
        CloudQueueMessage dlqMessage = new CloudQueueMessage(message);
        deadLetterQueue.addMessage(dlqMessage);
    }
}

