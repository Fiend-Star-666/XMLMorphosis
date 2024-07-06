package org.xml_to_db.queue;

public interface QueueService {
    void processMessage(String message);

    void sendToDeadLetterQueue(String message);
}
