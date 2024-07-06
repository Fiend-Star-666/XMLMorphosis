package org.xml_to_db.queue;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml_to_db.config.ConfigLoader;
import org.xml_to_db.utils.AwsClientHelper;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Data
public class SQSService implements QueueService {

    private static final Logger logger = LoggerFactory.getLogger(SQSService.class);
    private final AwsClientHelper awsClientHelper;
    private final String queueUrl;
    private final String dlqUrl;
    ConfigLoader config = ConfigLoader.getInstance();
    public SQSService() {
        String accessKey = config.getProperty("AWS_ACCESS_KEY_ID");
        String secretKey = config.getProperty("AWS_SECRET_ACCESS_KEY");
        String region = config.getProperty("AWS_REGION");
        this.queueUrl = config.getProperty("AWS_SQS_QUEUE_URL");
        this.dlqUrl = config.getProperty("AWS_SQS_DLQ_URL");

        if (accessKey == null || secretKey == null || region == null || queueUrl == null || dlqUrl == null) {
            throw new IllegalStateException("AWS SQS configuration is missing. Please set AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, AWS_REGION, AWS_SQS_QUEUE_URL, and AWS_SQS_DLQ_URL environment variables.");
        }

        this.awsClientHelper = new AwsClientHelper(accessKey, secretKey, region, queueUrl);
        logger.info("SQSService initialized with queue: {} and DLQ: {}", queueUrl, dlqUrl);
    }

    @Override
    public void processMessage(String message) {
        try {
            // Process the message
            logger.info("Processing message: {}", message);
            // If processing fails, send to DLQ
        } catch (Exception e) {
            logger.error("Error processing message. Sending to DLQ.", e);
            sendToDeadLetterQueue(message);
        }
    }

    @Override
    public void sendToDeadLetterQueue(String message) {
        try {
            SqsClient sqsClient = awsClientHelper.getSqsClient();
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(dlqUrl)
                    .messageBody(message)
                    .build();
            sqsClient.sendMessage(sendMessageRequest);
            logger.info("Message sent to DLQ: {}", dlqUrl);
        } catch (Exception e) {
            logger.error("Failed to send message to DLQ", e);
        }
    }

}
