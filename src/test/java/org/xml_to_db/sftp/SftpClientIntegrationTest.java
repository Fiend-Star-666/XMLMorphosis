package org.xml_to_db.sftp;

import org.junit.jupiter.api.*;
import org.xml_to_db.config.ConfigLoader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SftpClientIntegrationTest {

    private static SftpClient sftpClient;

    @BeforeAll
    static void setup() {
        ConfigLoader configLoader = ConfigLoader.getInstance();
        SftpConfig config = new SftpConfig(configLoader);
        sftpClient = new SftpClient(config);
    }

    @AfterAll
    static void tearDown() {
        if (sftpClient != null) {
            sftpClient.close();
        }
    }

    @Test
    @Order(1)
    void testConnect() {
        assertDoesNotThrow(() -> sftpClient.connect(),
                "Should connect successfully with valid credentials");
    }

    @Test
    @Order(2)
    void testGetChannel() {
        assertDoesNotThrow(() -> sftpClient.getChannel(),
                "Should return a valid channel after connecting");
    }
}
