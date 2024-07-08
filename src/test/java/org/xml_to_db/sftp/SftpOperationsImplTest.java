package org.xml_to_db.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SftpOperationsImplTest {

    private SftpOperationsImpl sftpOperations;
    private SftpClient mockSftpClient;
    private ChannelSftp mockChannel;

    @BeforeEach
    void setUp() {
        mockSftpClient = Mockito.mock(SftpClient.class);
        mockChannel = Mockito.mock(ChannelSftp.class);
        when(mockSftpClient.getChannel()).thenReturn(mockChannel);
        sftpOperations = new SftpOperationsImpl(mockSftpClient);
    }

    @Test
    void testUploadFile() throws SftpException {
        String localPath = "/local/path";
        String remotePath = "/remote/path";
        sftpOperations.uploadFile(localPath, remotePath);
        verify(mockChannel).put(localPath, remotePath);
    }

    @Test
    void testDownloadFile() throws SftpException {
        String remotePath = "/remote/path";
        String localPath = "/local/path";
        sftpOperations.downloadFile(remotePath, localPath);
        verify(mockChannel).get(remotePath, localPath);
    }

    @Test
    void testExists() throws SftpException {
        String remotePath = "/remote/path";
        when(mockChannel.lstat(remotePath)).thenReturn(mock(SftpATTRS.class));
        assertTrue(sftpOperations.exists(remotePath));
    }

    @Test
    void testExistsFileNotFound() throws SftpException {
        String remotePath = "/remote/path";
        when(mockChannel.lstat(remotePath)).thenThrow(new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE, "File not found"));
        assertFalse(sftpOperations.exists(remotePath));
    }

    @Test
    void testGetFilesAfterTime() throws SftpException {
        String remoteDir = "/remote/dir";
        long time = 1234567890L;

        Vector<ChannelSftp.LsEntry> mockEntries = new Vector<>();
        ChannelSftp.LsEntry mockEntry1 = mock(ChannelSftp.LsEntry.class);
        ChannelSftp.LsEntry mockEntry2 = mock(ChannelSftp.LsEntry.class);
        SftpATTRS mockAttrs1 = mock(SftpATTRS.class);
        SftpATTRS mockAttrs2 = mock(SftpATTRS.class);

        when(mockEntry1.getAttrs()).thenReturn(mockAttrs1);
        when(mockEntry2.getAttrs()).thenReturn(mockAttrs2);
        when(mockAttrs1.getMTime()).thenReturn((int) time + 100);
        when(mockAttrs2.getMTime()).thenReturn((int) time - 100);
        when(mockEntry1.getFilename()).thenReturn("file1");
        when(mockEntry2.getFilename()).thenReturn("file2");

        mockEntries.add(mockEntry1);
        mockEntries.add(mockEntry2);

        when(mockChannel.ls(remoteDir)).thenReturn(mockEntries);

        var result = sftpOperations.getFilesAfterTime(remoteDir, time);
        assertEquals(1, result.size());
        assertEquals("file1", result.getFirst());
    }
}
