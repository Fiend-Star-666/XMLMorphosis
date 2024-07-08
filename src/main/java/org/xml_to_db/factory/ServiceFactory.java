package org.xml_to_db.factory;

import org.xml_to_db.database.strategy.DatabaseStrategy;
import org.xml_to_db.database.strategy.DatabaseStrategyFactory;
import org.xml_to_db.queue.AzureQueueService;
import org.xml_to_db.queue.QueueService;
import org.xml_to_db.queue.SQSService;
import org.xml_to_db.storage.AzureBlobStorageService;
import org.xml_to_db.storage.S3StorageService;
import org.xml_to_db.storage.StorageService;

public class ServiceFactory {

    private ServiceFactory() {
    }

    public static QueueService getQueueService(String cloudProvider) throws Exception {
        return switch (cloudProvider.toLowerCase()) {
            case "azure" -> new AzureQueueService();
            case "aws" -> new SQSService();
            default -> throw new IllegalArgumentException("Unknown cloud provider: " + cloudProvider);
        };
    }

    public static StorageService getStorageService(String cloudProvider) throws Exception {
        return switch (cloudProvider.toLowerCase()) {
            case "azure" -> new AzureBlobStorageService();
            case "aws" -> new S3StorageService();
            default -> throw new IllegalArgumentException("Unknown cloud provider: " + cloudProvider);
        };
    }

    public static DatabaseStrategy getDatabaseStrategy(String dbType, String url, String username, String password) {
        return DatabaseStrategyFactory.getDatabaseStrategy(dbType, url, username, password);
    }
}
