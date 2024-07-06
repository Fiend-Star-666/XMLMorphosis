package org.xmlToDb.queue;

public interface QueueService {
    void processMessage(String message);
    void sendToDeadLetterQueue(String message);
}
