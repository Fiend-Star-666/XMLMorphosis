package org.xmlToDb.queue;

import org.xmlToDb.config.ConfigLoader;

public class QueueServiceFactory {
    private static final ConfigLoader config = ConfigLoader.getInstance();

    public static QueueService getQueueService() {
        String queueType = config.getProperty("QUEUE_TYPE");
        switch (queueType.toLowerCase()) {
            case "azure":
                return new AzureQueueService();
            case "aws":
                return new SQSService();
            default:
                throw new IllegalArgumentException("Unsupported queue type: " + queueType);
        }
    }

    private QueueServiceFactory() {
    }
}
