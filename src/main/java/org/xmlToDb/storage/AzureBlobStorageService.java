package org.xmlToDb.storage;

import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.xmlToDb.utils.AzureBlobHelper;

public class AzureBlobStorageService implements StorageService {

    private final CloudBlobContainer container;

    public AzureBlobStorageService() throws Exception {
        // Initialize Azure Blob container from environment variables
        String containerName = System.getenv("AZURE_BLOB_CONTAINER_NAME");
        this.container = AzureBlobHelper.getContainerReference(containerName);
    }

    @Override
    public void storeFile(String filePath) {
        // Logic to store file in Azure Blob Storage
    }
}

