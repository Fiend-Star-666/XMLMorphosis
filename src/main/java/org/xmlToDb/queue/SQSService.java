package org.xmlToDb.queue;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.xmlToDb.utils.AwsClientHelper;

public class SQSService implements QueueService {

    private final AmazonSQS sqs;
    private final String dlqUrl;

    public SQSService() {
        // Initialize AWS SQS and DLQ from environment variables
        this.sqs = AwsClientHelper.getSqsClient();
        this.dlqUrl = System.getenv("AWS_SQS_DLQ_URL");
    }

    @Override
    public void processMessage(String message) {
        try {
            // Process the message
            // If processing fails, send to DLQ
        } catch (Exception e) {
            sendToDeadLetterQueue(message);
        }
    }

    @Override
    public void sendToDeadLetterQueue(String message) {
        SendMessageRequest sendMsgRequest = new SendMessageRequest()
                .withQueueUrl(dlqUrl)
                .withMessageBody(message);
        sqs.sendMessage(sendMsgRequest);
    }
}

