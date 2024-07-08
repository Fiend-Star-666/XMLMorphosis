package org.xml_to_db.sftp;

import com.jcraft.jsch.JSchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class SftpClientTest {

    private SftpClient sftpClient;
    private SftpConfig mockConfig;

    @BeforeEach
    void setUp() {
        mockConfig = Mockito.mock(SftpConfig.class);
        when(mockConfig.getUsername()).thenReturn("testuser");
        when(mockConfig.getHost()).thenReturn("testhost");
        when(mockConfig.getPort()).thenReturn(22);
        when(mockConfig.getPassword()).thenReturn("testpassword");

        sftpClient = new SftpClient(mockConfig);
    }

    @Test
    void testConnectWithInvalidHost() {
        assertThrows(JSchException.class, () -> sftpClient.connect(),
                "Should throw JSchException for invalid host");
    }

    @Test
    void testGetChannelWhenNotConnected() {
        assertThrows(IllegalStateException.class, () -> sftpClient.getChannel(),
                "Should throw IllegalStateException when not connected");
    }

    @Test
    void testClose() {
        assertDoesNotThrow(() -> sftpClient.close(),
                "Close should not throw an exception");
    }

    @Test
    void testConnectWithInvalidCredentials() {
        when(mockConfig.getPassword()).thenReturn("wrongpassword");
        assertThrows(JSchException.class, () -> sftpClient.connect());
    }
}
