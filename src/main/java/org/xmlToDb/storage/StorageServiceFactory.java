package org.xmlToDb.storage;

public class StorageServiceFactory {
    public static StorageService getStorageService(String storageType) {
        switch (storageType.toLowerCase()) {
            case "azure":
                return new AzureBlobStorageService();
            case "aws":
                return new S3StorageService();
            default:
                throw new IllegalArgumentException("Unsupported storage type: " + storageType);
        }
    }

    private StorageServiceFactory() {
    }
}
