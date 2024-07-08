package org.xml_to_db.sftp;

import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.util.List;

public interface SftpOperations {
    void listFiles(String remoteDir) throws SftpException;

    void uploadFile(String localPath, String remotePath) throws SftpException;

    void downloadFile(String remotePath, String localPath) throws SftpException;

    void deleteFile(String remoteFile) throws SftpException;

    void renameFile(String oldPath, String newPath) throws SftpException;

    void createDirectory(String remoteDir) throws SftpException;

    void deleteDirectory(String remoteDir) throws SftpException;

    void chmod(int permissions, String remotePath) throws SftpException;

    boolean exists(String remotePath) throws SftpException;

    long getFileSize(String remotePath) throws SftpException;

    long getFileModificationTime(String remotePath) throws SftpException;

    String getFilePermissions(String remotePath) throws SftpException;

    void setFileModificationTime(String remotePath, int mtime) throws SftpException;

    void copyFileOrDirectory(String srcPath, String destPath) throws SftpException, IOException;

    String getFileType(String remotePath) throws SftpException;

    String getFileOwnerAndGroup(String remotePath) throws SftpException;

    List<String> getFilesAfterTime(String remoteDir, long time) throws SftpException;

    void moveFile(String srcPath, String destPath) throws SftpException;
}
