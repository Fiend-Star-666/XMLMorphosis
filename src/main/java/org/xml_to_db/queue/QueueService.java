package org.xml_to_db.queue;

public interface QueueService {
    void processMessage(String message);

    void sendToDeadLetterQueue(String message);

    /*
        +sendMessage(String): void
        +receiveMessages(int): List~String~
        +deleteMessage(String, String): void
        +getQueueLength(): int
        +createQueueIfNotExists(): void
        +deleteQueue(): void
     */
}
