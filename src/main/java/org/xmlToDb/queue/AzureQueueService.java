package org.xmlToDb.queue;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.models.SendMessageResult;
import org.xmlToDb.config.ConfigLoader;
import org.xmlToDb.utils.AzureQueueHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureQueueService implements QueueService {

    private static final Logger logger = LoggerFactory.getLogger(AzureQueueService.class);
    private final AzureQueueHelper queueHelper;
    private final AzureQueueHelper deadLetterQueueHelper;
    ConfigLoader config = ConfigLoader.getInstance();

    public AzureQueueService() {
        String connectionString = config.getProperty("AZURE_STORAGE_CONNECTION_STRING");
        String queueName = config.getProperty("AZURE_QUEUE_NAME");
        String dlqName = config.getProperty("AZURE_DLQ_NAME");

        if (connectionString == null || queueName == null || dlqName == null) {
            throw new IllegalStateException("Azure queue configuration is missing. Please set AZURE_STORAGE_CONNECTION_STRING, AZURE_QUEUE_NAME, and AZURE_DLQ_NAME environment variables.");
        }

        this.queueHelper = new AzureQueueHelper(connectionString, queueName);
        this.deadLetterQueueHelper = new AzureQueueHelper(connectionString, dlqName);

        logger.info("AzureQueueService initialized with queue: {} and DLQ: {}", queueName, dlqName);
    }

    @Override
    public void processMessage(String message) {
        try {
            // Process the message
            logger.info("Processing message: {}", message);
            // If processing fails, send to DLQ
        } catch (Exception e) {
            logger.error("Error processing message. Sending to DLQ.", e);
            sendToDeadLetterQueue(message);
        }
    }

    @Override
    public void sendToDeadLetterQueue(String message) {
        try {
            SendMessageResult result = deadLetterQueueHelper.sendMessage(message);
            logger.info("Message sent to DLQ. MessageId: {}", result.getMessageId());
        } catch (Exception e) {
            logger.error("Failed to send message to DLQ", e);
        }
    }

    public QueueClient getQueueClient() {
        return queueHelper.getQueueReference();
    }

    public QueueClient getDeadLetterQueueClient() {
        return deadLetterQueueHelper.getQueueReference();
    }
}