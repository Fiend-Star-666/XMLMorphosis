package org.xml_to_db.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.xml_to_db.config.ConfigLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class S3StorageService implements StorageService {
    private final AmazonS3 s3Client;
    private final String bucketName;
    ConfigLoader config = ConfigLoader.getInstance();

    public S3StorageService() {
        this.s3Client = AmazonS3ClientBuilder.defaultClient();
        this.bucketName = config.getProperty("AWS_S3_BUCKET_NAME");
    }

    @Override
    public List<String> listFiles(String fileExtension) {
        return s3Client.listObjects(bucketName).getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .filter(key -> key.endsWith(fileExtension))
                .collect(Collectors.toList());
    }

    @Override
    public String readFileContent(String filePath) {
        S3Object object = s3Client.getObject(bucketName, filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
        return reader.lines().collect(Collectors.joining("\n"));
    }

    @Override
    public void writeFile(String filePath, String content) {
        s3Client.putObject(bucketName, filePath, content);
    }

    @Override
    public void deleteFile(String filePath) {
        s3Client.deleteObject(bucketName, filePath);
    }
}
