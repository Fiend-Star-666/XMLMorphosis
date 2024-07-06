package org.xmlToDb.factory;

import org.xmlToDb.queue.AzureQueueService;
import org.xmlToDb.queue.QueueService;
import org.xmlToDb.queue.SQSService;
import org.xmlToDb.storage.AzureBlobStorageService;
import org.xmlToDb.storage.S3StorageService;
import org.xmlToDb.storage.StorageService;
import org.xmlToDb.strategy.DatabaseStrategy;
import org.xmlToDb.strategy.DatabaseStrategyFactory;

public class ServiceFactory {

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

