package org.xmlToDb.storage;

import com.amazonaws.services.s3.AmazonS3;
import org.xmlToDb.utils.AwsClientHelper;

public class S3StorageService implements StorageService {

    private final AmazonS3 s3;
    private final String bucketName;

    public S3StorageService() {
        // Initialize AWS S3 from environment variables
        this.s3 = AwsClientHelper.getS3Client();
        this.bucketName = System.getenv("AWS_S3_BUCKET_NAME");
    }

    @Override
    public void storeFile(String filePath) {
        // Logic to store file in AWS S3
    }
}

