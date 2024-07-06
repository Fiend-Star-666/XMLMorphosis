package org.xmlToDb.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

public class AwsClientHelper {
    private static final Logger logger = LoggerFactory.getLogger(AwsClientHelper.class);
    private final SqsClient sqsClient;
    private final String queueUrl;

    public AwsClientHelper(String accessKey, String secretKey, String region, String queueUrl) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        this.sqsClient = SqsClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of(region))
                .build();
        this.queueUrl = queueUrl;
        logger.info("AWS Client Helper initialized for queue: {}", queueUrl);
    }

    public SqsClient getSqsClient() {
        return sqsClient;
    }

    public void sendMessage(String message) {
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
        sqsClient.sendMessage(sendMsgRequest);
        logger.info("Message sent to AWS SQS Queue");
    }

    public List<Message> receiveMessages(int maxMessages) {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(maxMessages)
                .build();
        return sqsClient.receiveMessage(receiveMessageRequest).messages();
    }

    public void deleteMessage(Message message) {
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);
        logger.info("Message deleted from AWS SQS Queue. ReceiptHandle: {}", message.receiptHandle());
    }

    public int getQueueLength() {
        GetQueueAttributesRequest getQueueAttributesRequest = GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
                .build();
        return Integer.parseInt(sqsClient.getQueueAttributes(getQueueAttributesRequest)
                .attributes()
                .get(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES));
    }

    public void createQueueIfNotExists(String queueName) {
        try {
            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();
            sqsClient.getQueueUrl(getQueueRequest);
        } catch (QueueDoesNotExistException e) {
            CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();
            sqsClient.createQueue(createQueueRequest);
            logger.info("AWS SQS Queue created: {}", queueName);
        }
    }

    public void deleteQueue() {
        DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder()
                .queueUrl(queueUrl)
                .build();
        sqsClient.deleteQueue(deleteQueueRequest);
        logger.info("AWS SQS Queue deleted: {}", queueUrl);
    }

    public void close() {
        sqsClient.close();
        logger.info("AWS SQS Client closed");
    }
}