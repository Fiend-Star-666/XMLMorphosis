package org.xmlToDb.sftp;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SftpClientTest {
    private static final String REMOTE_PATH = "/testFolder/test";
    private static final String LOCAL_PATH = "src/main/resources/schemas/simple-schema.xsd";
    private static final String TEST_FOLDER = "/testFolder";
    private static final int MODIFICATION_TIME = 1234567890;
    private static final long TIME = 1234567890L;

    public static void main(String[] args) {
        SftpClient client = new SftpClient();

        try {
            authenticate(client);
            performFileOperations(client);
            client.close();
            log.info("Disconnected from the server");
        } catch (Exception e) {
            log.error("An error occurred", e);
        }
    }

    private static void authenticate(SftpClient client) throws Exception {
        client.authPassword();
        log.info("Authenticated with password");
    }

    private static void performFileOperations(SftpClient client) throws Exception {
//        listFiles(client);
//        uploadFile(client);
        downloadFile(client);
        deleteFile(client);
        renameFile(client);
        createDirectory(client);
        deleteDirectory(client);
        changePermissions(client);
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
    }

    private static void listFiles(SftpClient client) throws Exception {
        client.listFiles(TEST_FOLDER);
        log.info("Listed all files in the root directory");
    }

    private static void uploadFile(SftpClient client) throws Exception {
        client.uploadFile(LOCAL_PATH, REMOTE_PATH);
        log.info("Uploaded a file");
    }

    private static void downloadFile(SftpClient client) throws Exception {
        client.downloadFile(REMOTE_PATH+"/simple-schema.xsd", LOCAL_PATH);
        log.info("Downloaded a file");
    }

    private static void deleteFile(SftpClient client) throws Exception {
        client.delete(REMOTE_PATH);
        log.info("Deleted a file");
    }

    private static void renameFile(SftpClient client) throws Exception {
        client.renameFile("old_file_path", "new_file_path");
        log.info("Renamed a file");
    }

    private static void createDirectory(SftpClient client) throws Exception {
        client.createDirectory("remote_directory_path");
        log.info("Created a directory");
    }

    private static void deleteDirectory(SftpClient client) throws Exception {
        client.deleteDirectory("remote_directory_path");
        log.info("Deleted a directory");
    }

    private static void changePermissions(SftpClient client) throws Exception {
        client.chmod(755, REMOTE_PATH);
        log.info("Changed permissions of a file");
    }

    private static void checkFileExistence(SftpClient client) throws Exception {
        boolean exists = client.exists(REMOTE_PATH);
        log.info("Checked if a file exists: " + exists);
    }

    private static void getFileSize(SftpClient client) throws Exception {
        long size = client.getFileSize(REMOTE_PATH);
        log.info("Got the size of a file: " + size);
    }

    private static void getFileModificationTime(SftpClient client) throws Exception {
        long mtime = client.getFileModificationTime(REMOTE_PATH);
        log.info("Got the modification time of a file: " + mtime);
    }

    private static void getFilePermissions(SftpClient client) throws Exception {
        String permissions = client.getFilePermissions(REMOTE_PATH);
        log.info("Got the permissions of a file: " + permissions);
    }

    private static void setFileModificationTime(SftpClient client) throws Exception {
        client.setFileModificationTime(REMOTE_PATH, MODIFICATION_TIME);
        log.info("Set the modification time of a file");
    }

    private static void copyFileOrDirectory(SftpClient client) throws Exception {
        client.copyFileOrDirectory("src_file_or_directory_path", "dest_file_or_directory_path");
        log.info("Copied a file or directory");
    }

    private static void getFileType(SftpClient client) throws Exception {
        String type = client.getFileType(REMOTE_PATH);
        log.info("Got the type of a file: " + type);
    }

    private static void getFileOwnerAndGroup(SftpClient client) throws Exception {
        String ownerGroup = client.getFileOwnerAndGroup(REMOTE_PATH);
        log.info("Got the owner and group of a file: " + ownerGroup);
    }

    private static void getFilesAfterTime(SftpClient client) throws Exception {
        List<String> files = client.getFilesAfterTime("remote_directory_path", TIME);
        log.info("Got a list of files in a directory modified after a certain time: " + files);
    }

    private static void moveFile(SftpClient client) throws Exception {
        client.moveFile("src_file_path", "dest_file_path");
        log.info("Moved a file");
    }
}
