package org.xmlToDb.utils;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.models.QueueMessageItem;
import com.azure.storage.queue.models.SendMessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class AzureQueueHelper {
    private static final Logger logger = LoggerFactory.getLogger(AzureQueueHelper.class);
    private final QueueClient queueClient;

    public AzureQueueHelper(String connectionString, String queueName) {
        this.queueClient = new QueueClientBuilder()
                .connectionString(connectionString)
                .queueName(queueName)
                .buildClient();
        logger.info("Azure Queue Helper initialized for queue: {}", queueName);
    }

    public QueueClient getQueueReference() {
        return queueClient;
    }

    public SendMessageResult sendMessage(String message) {
        SendMessageResult result = queueClient.sendMessage(message);
        logger.info("Message sent to Azure Queue. MessageId: {}", result.getMessageId());
        return result;
    }

    // Method for sending to dead letter queue
    public SendMessageResult sendToDeadLetterQueue(String message) {
        // Assuming we're using the same client for DLQ, just with a different queue name
        // In a real-world scenario, you might want to create a separate client for the DLQ
        SendMessageResult result = queueClient.sendMessage(message);
        logger.info("Message sent to Dead Letter Queue. MessageId: {}", result.getMessageId());
        return result;
    }

    public List<QueueMessageItem> receiveMessages(int maxMessages) {
        return queueClient.receiveMessages(maxMessages).stream().collect(Collectors.toList());
    }

    public void deleteMessage(QueueMessageItem message) {
        queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
        logger.info("Message deleted from Azure Queue. MessageId: {}", message.getMessageId());
    }

    public long getQueueLength() {
        return queueClient.getProperties().getApproximateMessagesCount();
    }

    public void createQueueIfNotExists() {
        queueClient.createIfNotExists();
        logger.info("Azure Queue created or confirmed to exist: {}", queueClient.getQueueName());
    }

    public void deleteQueue() {
        queueClient.delete();
        logger.info("Azure Queue deleted: {}", queueClient.getQueueName());
    }
}