package org.xmlToDb.sftp;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

@Slf4j
public class SftpClientTest {
    private static final String LOCAL_PATH = "src/main/resources/schemas/test-schema.xsd";
    private static final String REMOTE_DIRECTORY = "/testFolder/test";
    private static final String REMOTE_FILE_PATH = REMOTE_DIRECTORY + "/test";
    private static final int MODIFICATION_TIME = 1234567890;
    private static final long TIME = 1234567890L;
    private static final String fileRenamed = "/test-renamed";

    public static void main(String[] args) {
        SftpClient client = new SftpClient();

        try {
            // Create test files and directories locally
            createTestFiles();

            authenticate(client);
            performFileOperations(client);
            client.close();
            log.info("Disconnected from the server");

            // Clean up local test files
            deleteTestFiles();
        } catch (Exception e) {
            log.error("An error occurred", e);
        }
    }

    private static void createTestFiles() throws Exception {
        File testFile = new File(LOCAL_PATH);
        if (!testFile.exists()) {
            testFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(testFile)) {
                writer.write("<schema xmlns=\"http://www.w3.org/2001/XMLSchema\"></schema>");
            }
        }
        log.info("Created test file: " + LOCAL_PATH);
    }

    private static void deleteTestFiles() {
        File testFile = new File(LOCAL_PATH);
        if (testFile.exists()) {
            testFile.delete();
            log.info("Deleted test file: " + LOCAL_PATH);
        }
    }

    private static void authenticate(SftpClient client) throws Exception {
        client.authPassword();
        log.info("Authenticated with password");
    }

    private static void performFileOperations(SftpClient client) throws Exception {
        createDirectory(client);
        listFiles(client);
        uploadFile(client);
        downloadFile(client);
        renameFile(client);
//        changePermissions(client);
        checkFileExistence(client);
        getFileSize(client);
        getFileModificationTime(client);
        getFilePermissions(client);
        setFileModificationTime(client);
        copyFileOrDirectory(client);
        getFileType(client);
        getFileOwnerAndGroup(client);
        getFilesAfterTime(client);
        moveFile(client);
        deleteFile(client);
        deleteDirectory(client);
    }

    private static void listFiles(SftpClient client) throws Exception {
        client.listFiles(REMOTE_DIRECTORY);
        log.info("Listed all files in the remote directory");
    }

    private static void uploadFile(SftpClient client) throws Exception {
        client.uploadFile(LOCAL_PATH, REMOTE_FILE_PATH);
        log.info("Uploaded a file to " + REMOTE_FILE_PATH);
    }

    private static void downloadFile(SftpClient client) throws Exception {
        client.downloadFile(REMOTE_FILE_PATH, LOCAL_PATH);
        log.info("Downloaded a file from " + REMOTE_FILE_PATH);
    }

    private static void deleteFile(SftpClient client) throws Exception {
        client.delete(REMOTE_FILE_PATH);
        log.info("Deleted the file at " + REMOTE_FILE_PATH);
    }

    private static void renameFile(SftpClient client) throws Exception {
        String newRemoteFilePath = REMOTE_DIRECTORY + "/test-renamed";
        client.renameFile(REMOTE_FILE_PATH, newRemoteFilePath);
        log.info("Renamed the file to " + newRemoteFilePath);
    }

    private static void createDirectory(SftpClient client) throws Exception {
        client.createDirectory(REMOTE_DIRECTORY);
        log.info("Created remote directory: " + REMOTE_DIRECTORY);
    }

    private static void deleteDirectory(SftpClient client) throws Exception {
        client.deleteDirectory(REMOTE_DIRECTORY);
        log.info("Deleted the remote directory: " + REMOTE_DIRECTORY);
    }

    private static void changePermissions(SftpClient client) throws Exception {
        client.chmod(755, REMOTE_FILE_PATH);
        log.info("Changed permissions of the file at " + REMOTE_FILE_PATH);
    }

    private static void checkFileExistence(SftpClient client) throws Exception {
        boolean exists = client.exists(REMOTE_FILE_PATH + fileRenamed);
        log.info("Checked if the file exists at " + REMOTE_FILE_PATH + ": " + exists);
    }

    private static void getFileSize(SftpClient client) throws Exception {
        long size = client.getFileSize(REMOTE_FILE_PATH + fileRenamed);
        log.info("Got the size of the file at " + REMOTE_FILE_PATH + ": " + size);
    }

    private static void getFileModificationTime(SftpClient client) throws Exception {
        long mtime = client.getFileModificationTime(REMOTE_FILE_PATH + fileRenamed);
        log.info("Got the modification time of the file at " + REMOTE_FILE_PATH + ": " + mtime);
    }

    private static void getFilePermissions(SftpClient client) throws Exception {
        String permissions = client.getFilePermissions(REMOTE_FILE_PATH + fileRenamed);
        log.info("Got the permissions of the file at " + REMOTE_FILE_PATH + ": " + permissions);
    }

    private static void setFileModificationTime(SftpClient client) throws Exception {
        client.setFileModificationTime(REMOTE_FILE_PATH, MODIFICATION_TIME);
        log.info("Set the modification time of the file at " + REMOTE_FILE_PATH);
    }

    private static void copyFileOrDirectory(SftpClient client) throws Exception {
        String destFilePath = REMOTE_DIRECTORY + "/test-copy";
        client.copyFileOrDirectory(REMOTE_FILE_PATH, destFilePath);
        log.info("Copied the file to " + destFilePath);
    }

    private static void getFileType(SftpClient client) throws Exception {
        String type = client.getFileType(REMOTE_FILE_PATH);
        log.info("Got the type of the file at " + REMOTE_FILE_PATH + ": " + type);
    }

    private static void getFileOwnerAndGroup(SftpClient client) throws Exception {
        String ownerGroup = client.getFileOwnerAndGroup(REMOTE_FILE_PATH);
        log.info("Got the owner and group of the file at " + REMOTE_FILE_PATH + ": " + ownerGroup);
    }

    private static void getFilesAfterTime(SftpClient client) throws Exception {
        List<String> files = client.getFilesAfterTime(REMOTE_DIRECTORY, TIME);
        log.info("Got a list of files in the directory modified after a certain time: " + files);
    }

    private static void moveFile(SftpClient client) throws Exception {
        String destFilePath = REMOTE_DIRECTORY + "/test-moved";
        client.moveFile(REMOTE_FILE_PATH, destFilePath);
        log.info("Moved the file to " + destFilePath);
    }
}
