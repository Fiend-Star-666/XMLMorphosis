package org.xml_to_db.queue;

import lombok.extern.slf4j.Slf4j;
import org.xml_to_db.config.ConfigLoader;

@Slf4j
public class QueueServiceFactory {
    private static final ConfigLoader config = ConfigLoader.getInstance();

    private QueueServiceFactory() {
        // Private constructor to prevent instantiation
    }

    public static QueueService getQueueService(String queueType) {
        log.info("Creating QueueService for type: {}", queueType);
        return switch (queueType.toLowerCase()) {
            case "azure" -> new AzureQueueService();
            case "aws" -> new SQSService();
            default -> {
                log.error("Unsupported queue type: {}", queueType);
                throw new IllegalArgumentException("Unsupported queue type: " + queueType);
            }
        };
    }
}