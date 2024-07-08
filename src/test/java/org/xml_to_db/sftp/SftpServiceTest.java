package org.xml_to_db.sftp;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.xml_to_db.config.ConfigLoader;

import java.io.File;
import java.io.FileWriter;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SftpServiceTest {

    public static final int ALLOWED_TIME_DIFFERENCE = 5; // 5 seconds
    private static final String LOCAL_PATH = "src/test/resources/test-schema.xsd";
    private static final String REMOTE_DIRECTORY = "/testFolder";
    private static final String REMOTE_FILE_NAME = "test-file.xsd";
    private static final String REMOTE_FILE_PATH = REMOTE_DIRECTORY + "/" + REMOTE_FILE_NAME;
    private static final String FILE_RENAMED = "test-file-renamed.xsd";
    private static int MODIFICATION_TIME;
    private static long TIME;
    private static SftpService sftpService;

    @BeforeAll
    static void setup() {
        TIME = (int) Instant.now().getEpochSecond() - 3600; // 1 hour ago
        ConfigLoader configLoader = ConfigLoader.getInstance();
        sftpService = new SftpServiceImpl(configLoader);
        try {
            sftpService.connect();
            log.info("Connected to the server");
            createTestFiles();
            createRemoteTestDirectory();
        } catch (Exception e) {
            log.error("Failed to set up SFTP connection: {}", e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            if (sftpService != null) {
                deleteRemoteFiles();
                sftpService.disconnect();
                log.info("Disconnected from the server");
            }
        } catch (Exception e) {
            log.error("Error during teardown", e);
        } finally {
            deleteLocalTestFiles();
        }
    }

    private static void deleteRemoteFiles() {
        try {
            if (sftpService.exists(REMOTE_DIRECTORY)) {
                List<String> files = sftpService.getFilesAfterTime(REMOTE_DIRECTORY, 0);
                for (String file : files) {
                    if (!".".equals(file) && !"..".equals(file)) {
                        try {
                            sftpService.deleteFile(REMOTE_DIRECTORY + "/" + file);
                            log.info("Deleted remote file: {}", file);
                        } catch (Exception e) {
                            log.warn("Failed to delete remote file: {}", file, e);
                        }
                    }
                }
                try {
                    sftpService.deleteDirectory(REMOTE_DIRECTORY);
                    log.info("Deleted remote directory: {}", REMOTE_DIRECTORY);
                } catch (Exception e) {
                    log.warn("Failed to delete remote directory: {}", REMOTE_DIRECTORY, e);
                }
            }
        } catch (Exception e) {
            log.error("Error during remote file cleanup", e);
        }
    }

    private static void createRemoteTestDirectory() {
        try {
            sftpService.createDirectory(REMOTE_DIRECTORY);
            log.info("Created remote test directory: " + REMOTE_DIRECTORY);
        } catch (Exception e) {
            log.error("Failed to create remote test directory", e);
        }
    }

    private static void createTestFiles() throws Exception {
        File testFile = new File(LOCAL_PATH);
        testFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("<schema xmlns=\"http://www.w3.org/2001/XMLSchema\"></schema>");
        }
        log.info("Created test file: " + LOCAL_PATH);
    }

    private static void deleteLocalTestFiles() {
        File testFile = new File(LOCAL_PATH);
        if (testFile.exists() && testFile.delete()) {
            log.info("Deleted local test file: " + LOCAL_PATH);
        }
    }

    private static void createLargeFile(String path, long size) throws Exception {
        try (FileWriter writer = new FileWriter(path)) {
            for (long i = 0; i < size; i++) {
                writer.write('a');
            }
        }
    }

    private static void ensureRemoteDirectoryExists() throws Exception {
        if (!sftpService.exists(REMOTE_DIRECTORY)) {
            sftpService.createDirectory(REMOTE_DIRECTORY);
            log.info("Recreated remote directory: " + REMOTE_DIRECTORY);
        }
    }

    private static void deleteRemoteFiles(String directory) throws Exception {
        if (!sftpService.exists(directory)) {
            return;
        }
        List<String> files = sftpService.getFilesAfterTime(directory, 0);
        for (String file : files) {
            if (!".".equals(file) && !"..".equals(file)) {
                String fullPath = directory + "/" + file;
                try {
                    if ("Directory".equals(sftpService.getFileType(fullPath))) {
                        deleteRemoteFiles(fullPath);
                    } else {
                        sftpService.deleteFile(fullPath);
                        log.info("Deleted remote file: " + fullPath);
                    }
                } catch (Exception e) {
                    log.warn("Failed to delete {}: {}", fullPath, e.getMessage());
                }
            }
        }
        try {
            sftpService.deleteDirectory(directory);
            log.info("Deleted remote directory: " + directory);
        } catch (Exception e) {
            log.warn("Failed to delete directory {}: {}", directory, e.getMessage());
        }
    }

    private static boolean isDirectoryEmpty(String directory) throws Exception {
        List<String> files = sftpService.getFilesAfterTime(directory, 0);
        return files.size() <= 2; // Accounting for "." and ".." entries
    }

    @Test
    @Order(1)
    void testCreateDirectory() throws Exception {
        String newDir = REMOTE_DIRECTORY + "/newDir";
        try {
            sftpService.createDirectory(newDir);
            assertTrue(sftpService.exists(newDir));
            assertTrue(isDirectoryEmpty(newDir));
            log.info("Created new remote directory: " + newDir);
        } finally {
            try {
                deleteRemoteFiles(newDir);
            } catch (Exception e) {
                log.warn("Failed to clean up directory {}: {}", newDir, e.getMessage());
            }
        }
    }

    @Test
    @Order(2)
    void testUploadFile() throws Exception {
        sftpService.uploadFile(LOCAL_PATH, REMOTE_FILE_PATH);
        assertTrue(sftpService.exists(REMOTE_FILE_PATH));
        log.info("Uploaded a file to " + REMOTE_FILE_PATH);
    }

    @Test
    @Order(3)
    void testListFiles() throws Exception {
        List<String> files = sftpService.getFilesAfterTime(REMOTE_DIRECTORY, 0);
        assertTrue(files.contains(REMOTE_FILE_NAME));
        log.info("Listed all files in the remote directory");
    }

    @Test
    @Order(4)
    void testDownloadFile() throws Exception {
        String localDownloadPath = LOCAL_PATH.replace(".xsd", "-downloaded.xsd");
        sftpService.downloadFile(REMOTE_FILE_PATH, localDownloadPath);
        File downloadedFile = new File(localDownloadPath);
        assertTrue(downloadedFile.exists());
        log.info("Downloaded a file from " + REMOTE_FILE_PATH + " to " + localDownloadPath);
        downloadedFile.delete(); // Clean up downloaded file
    }

    @Test
    @Order(5)
    void testRenameFile() throws Exception {
        String newRemoteFilePath = REMOTE_DIRECTORY + "/" + FILE_RENAMED;
        sftpService.renameFile(REMOTE_FILE_PATH, newRemoteFilePath);
        assertFalse(sftpService.exists(REMOTE_FILE_PATH));
        assertTrue(sftpService.exists(newRemoteFilePath));
        log.info("Renamed the file from " + REMOTE_FILE_PATH + " to " + newRemoteFilePath);
    }

    @Test
    @Order(6)
    void testGetFileSize() throws Exception {
        String filePath = REMOTE_DIRECTORY + "/" + FILE_RENAMED;
        long size = sftpService.getFileSize(filePath);
        assertTrue(size > 0);
        log.info("Got the size of the file at " + filePath + ": " + size + " bytes");
    }

    @Test
    @Order(7)
    void testSetAndGetFileModificationTime() throws Exception {
        String filePath = REMOTE_DIRECTORY + "/" + FILE_RENAMED;
        MODIFICATION_TIME = (int) Instant.now().getEpochSecond();

        sftpService.setFileModificationTime(filePath, MODIFICATION_TIME);
        int mtime = (int) sftpService.getFileModificationTime(filePath);
        int timeDifference = Math.abs(MODIFICATION_TIME - mtime);

        log.info("GMT time: {}, Set time: {}, Got time: {}, Difference: {} seconds",
                MODIFICATION_TIME, MODIFICATION_TIME, mtime, timeDifference);

        assertAll(
                () -> assertTrue(timeDifference <= ALLOWED_TIME_DIFFERENCE,
                        "Time difference (%d s) exceeds allowed difference (%d s)".formatted(
                                timeDifference, ALLOWED_TIME_DIFFERENCE)),
                () -> assertTrue(mtime <= Instant.now().getEpochSecond(),
                        "Set time (%d) is in the future".formatted(mtime))
        );
    }

    @Test
    @Order(8)
    void testCopyFileOrDirectory() throws Exception {
        String srcPath = REMOTE_DIRECTORY + "/" + FILE_RENAMED;
        String destPath = REMOTE_DIRECTORY + "/test-copy.xsd";
        sftpService.copyFileOrDirectory(srcPath, destPath);
        assertTrue(sftpService.exists(destPath));
        log.info("Copied the file from " + srcPath + " to " + destPath);
    }

    @Test
    @Order(9)
    void testGetFileType() throws Exception {
        String filePath = REMOTE_DIRECTORY + "/" + FILE_RENAMED;
        String type = sftpService.getFileType(filePath);
        assertEquals("File", type);
        log.info("Got the type of the file at " + filePath + ": " + type);
    }

    @Test
    @Order(10)
    void testGetFileOwnerAndGroup() throws Exception {
        String filePath = REMOTE_DIRECTORY + "/" + FILE_RENAMED;
        String ownerGroup = sftpService.getFileOwnerAndGroup(filePath);
        assertNotNull(ownerGroup);
        log.info("Got the owner and group of the file at " + filePath + ": " + ownerGroup);
    }

    @Test
    @Order(11)
    void testGetFilesAfterTime() throws Exception {
        List<String> files = sftpService.getFilesAfterTime(REMOTE_DIRECTORY, TIME);
        assertFalse(files.isEmpty());
        log.info("Got a list of files in the directory modified after " + TIME + ": " + files);
    }

    @Test
    @Order(12)
    void testMoveFile() throws Exception {
        String srcPath = REMOTE_DIRECTORY + "/" + FILE_RENAMED;
        String destPath = REMOTE_DIRECTORY + "/test-moved.xsd";
        sftpService.moveFile(srcPath, destPath);
        assertFalse(sftpService.exists(srcPath));
        assertTrue(sftpService.exists(destPath));
        log.info("Moved the file from " + srcPath + " to " + destPath);
    }

    @Test
    @Order(13)
    void testUploadLargeFile() throws Exception {
        String largePath = "src/test/resources/large-test-file.bin";
        createLargeFile(largePath, 5 * 1024 * 1024); // 5 MB file
        String remoteFile = REMOTE_DIRECTORY + "/large-file.bin";
        sftpService.uploadFile(largePath, remoteFile);
        assertTrue(sftpService.exists(remoteFile));
        assertEquals(5 * 1024 * 1024, sftpService.getFileSize(remoteFile));
        new File(largePath).delete();
    }

    @Test
    @Order(14)
    void testUploadEmptyFile() throws Exception {
        String emptyPath = "src/test/resources/empty-file.txt";
        new File(emptyPath).createNewFile();
        String remoteFile = REMOTE_DIRECTORY + "/empty-file.txt";
        sftpService.uploadFile(emptyPath, remoteFile);
        assertTrue(sftpService.exists(remoteFile));
        assertEquals(0, sftpService.getFileSize(remoteFile));
        new File(emptyPath).delete();
    }

    @Test
    @Order(15)
    void testUploadNonExistentFile() {
        String nonExistentPath = "src/test/resources/non-existent-file.txt";
        String remoteFile = REMOTE_DIRECTORY + "/non-existent-file.txt";
        assertThrows(Exception.class, () -> sftpService.uploadFile(nonExistentPath, remoteFile));
    }

    @Test
    @Order(16)
    void testListFilesInEmptyDirectory() throws Exception {
        String emptyDir = REMOTE_DIRECTORY + "/empty-dir";
        sftpService.createDirectory(emptyDir);
        List<String> files = sftpService.getFilesAfterTime(emptyDir, 0);
        assertTrue(files.isEmpty() || (files.size() == 2 && files.contains(".") && files.contains("..")));
        sftpService.deleteDirectory(emptyDir);
    }

    @Test
    @Order(17)
    void testMoveFileToNonExistentDirectory() throws Exception {
        ensureRemoteDirectoryExists();
        String srcPath = REMOTE_DIRECTORY + "/test-file-for-move.txt";
        String nonExistentDir = REMOTE_DIRECTORY + "/non-existent-dir/";
        String destPath = nonExistentDir + "moved-file.xsd";
        try {
            sftpService.uploadFile(LOCAL_PATH, srcPath);
            assertTrue(sftpService.exists(srcPath), "Source file should exist before move");

            // Attempt to move the file
            sftpService.moveFile(srcPath, destPath);

            // Verify that the move was successful
            assertFalse(sftpService.exists(srcPath), "Source file should not exist after move");
            assertTrue(sftpService.exists(destPath), "Destination file should exist after move");
            assertTrue(sftpService.exists(nonExistentDir), "Non-existent directory should have been created");

            log.info("File successfully moved to non-existent directory, which was created automatically");
        } finally {
            // Clean up
            if (sftpService.exists(srcPath)) {
                sftpService.deleteFile(srcPath);
            }
            if (sftpService.exists(destPath)) {
                sftpService.deleteFile(destPath);
            }
            if (sftpService.exists(nonExistentDir)) {
                deleteRemoteFiles(nonExistentDir);
            }
        }
    }

    @Test
    @Order(18)
    void testGetFileTypeForDirectory() throws Exception {
        ensureRemoteDirectoryExists();
        String dirType = sftpService.getFileType(REMOTE_DIRECTORY);
        assertEquals("Directory", dirType);
    }

    @Test
    @Order(19)
    void testCleanup() throws Exception {
        deleteRemoteFiles(REMOTE_DIRECTORY);
        assertFalse(sftpService.exists(REMOTE_DIRECTORY));
        log.info("Cleanup test passed");
    }

}
