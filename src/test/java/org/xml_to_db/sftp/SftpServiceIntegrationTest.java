package org.xml_to_db.sftp;

import org.junit.jupiter.api.*;
import org.xml_to_db.config.ConfigLoader;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SftpServiceIntegrationTest {

    private static final String LOCAL_PATH = "src/test/resources/test-schema.xsd";
    private static final String REMOTE_DIRECTORY = "/testFolder";
    private static final String REMOTE_FILE_NAME = "test-file.xsd";
    private static final String REMOTE_FILE_PATH = REMOTE_DIRECTORY + "/" + REMOTE_FILE_NAME;
    private static final String FILE_RENAMED = "test-file-renamed.xsd";
    private static final int ALLOWED_TIME_DIFFERENCE = 300;

    private static SftpService sftpService;

    @BeforeAll
    static void setup() throws Exception {
        ConfigLoader configLoader = ConfigLoader.getInstance();
        sftpService = new SftpServiceImpl(configLoader);
        sftpService.connect();
        createTestFiles();
    }

    @AfterAll
    static void tearDown() throws Exception {
        deleteRemoteFiles();
        sftpService.disconnect();
        deleteLocalTestFiles();
    }

    private static void createTestFiles() throws Exception {
        File testFile = new File(LOCAL_PATH);
        testFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("<schema xmlns=\"http://www.w3.org/2001/XMLSchema\"></schema>");
        }
    }

    private static void deleteLocalTestFiles() {
        new File(LOCAL_PATH).delete();
    }

    private static void deleteRemoteFiles() throws Exception {
        List<String> files = sftpService.getFilesAfterTime(REMOTE_DIRECTORY, 0);
        for (String file : files) {
            if (!".".equals(file) && !"..".equals(file)) {
                sftpService.deleteFile(REMOTE_DIRECTORY + "/" + file);
            }
        }
        sftpService.deleteDirectory(REMOTE_DIRECTORY);
    }

    @Test
    @Order(1)
    void testCreateDirectoryAndUploadFile() throws Exception {
        sftpService.createDirectory(REMOTE_DIRECTORY);
        sftpService.uploadFile(LOCAL_PATH, REMOTE_FILE_PATH);
        assertTrue(sftpService.exists(REMOTE_FILE_PATH));
    }

    @Test
    @Order(2)
    void testDownloadAndCompareFile() throws Exception {
        String downloadPath = LOCAL_PATH + ".download";
        sftpService.downloadFile(REMOTE_FILE_PATH, downloadPath);
        assertTrue(Files.exists(new File(downloadPath).toPath()));
        assertArrayEquals(Files.readAllBytes(new File(LOCAL_PATH).toPath()),
                Files.readAllBytes(new File(downloadPath).toPath()));
        new File(downloadPath).delete();
    }

    @Test
    @Order(3)
    void testRenameAndCheckFileExists() throws Exception {
        String newRemoteFilePath = REMOTE_DIRECTORY + "/" + FILE_RENAMED;
        sftpService.renameFile(REMOTE_FILE_PATH, newRemoteFilePath);
        assertFalse(sftpService.exists(REMOTE_FILE_PATH));
        assertTrue(sftpService.exists(newRemoteFilePath));
    }

    @Test
    @Order(4)
    void testSetAndGetFileModificationTime() throws Exception {
        String filePath = REMOTE_DIRECTORY + "/" + FILE_RENAMED;
        int currentTime = (int) Instant.now().getEpochSecond();
        sftpService.setFileModificationTime(filePath, currentTime);
        long mtime = sftpService.getFileModificationTime(filePath);
        assertTrue(Math.abs(currentTime - mtime) <= ALLOWED_TIME_DIFFERENCE);
    }

    @Test
    @Order(5)
    void testListFilesAfterTime() throws Exception {
        List<String> files = sftpService.getFilesAfterTime(REMOTE_DIRECTORY, 0);
        assertTrue(files.contains(FILE_RENAMED));
    }
}
