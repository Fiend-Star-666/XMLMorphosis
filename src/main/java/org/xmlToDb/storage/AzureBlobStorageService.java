package org.xmlToDb.storage;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.xmlToDb.config.ConfigLoader;

import java.util.List;
import java.util.stream.Collectors;

public class AzureBlobStorageService implements StorageService {
    private final BlobContainerClient containerClient;
    ConfigLoader config = ConfigLoader.getInstance();
    public AzureBlobStorageService() {
        String connectionString = config.getProperty("AZURE_STORAGE_CONNECTION_STRING");
        String containerName = config.getProperty("AZURE_STORAGE_CONTAINER_NAME");

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
    }

    @Override
    public List<String> listFiles(String fileExtension) {
        return containerClient.listBlobs().stream()
                .map(blobItem -> blobItem.getName())
                .filter(name -> name.endsWith(fileExtension))
                .collect(Collectors.toList());
    }

    @Override
    public String readFileContent(String filePath) {
        return containerClient.getBlobClient(filePath)
                .downloadContent()
                .toString();
    }

    @Override
    public void writeFile(String filePath, String content) {
        containerClient.getBlobClient(filePath)
                .upload(BinaryData.fromString(content));
    }

    @Override
    public void deleteFile(String filePath) {
        containerClient.getBlobClient(filePath).delete();
    }
}