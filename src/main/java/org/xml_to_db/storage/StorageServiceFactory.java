package org.xml_to_db.storage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StorageServiceFactory {

    private StorageServiceFactory() {
        // Private constructor to prevent instantiation
    }

    public static StorageService getStorageService(String storageType) {
        log.info("Creating StorageService for type: {}", storageType);
        return switch (storageType.toLowerCase()) {
            case "azure" -> new AzureBlobStorageService();
            case "aws" -> new S3StorageService();
            default -> {
                log.error("Unsupported storage type: {}", storageType);
                throw new IllegalArgumentException("Unsupported storage type: " + storageType);
            }
        };
    }
}
