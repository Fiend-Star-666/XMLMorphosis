package org.xml_to_db.sftp;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.xml_to_db.config.ConfigLoader;

import java.io.IOException;
import java.util.List;

@Slf4j
public class SftpServiceImpl implements SftpService {
    private final SftpClient sftpClient;
    private final SftpOperations sftpOperations;

    public SftpServiceImpl(ConfigLoader configLoader) {
        SftpConfig config = new SftpConfig(configLoader);
        this.sftpClient = new SftpClient(config);
        this.sftpOperations = new SftpOperationsImpl(sftpClient);
    }

    @Override
    public void connect() throws Exception {
        sftpClient.connect();
    }

    @Override
    public void disconnect() {
        sftpClient.close();
    }

    @Override
    public void listFiles(String remoteDir) throws SftpException {
        sftpOperations.listFiles(remoteDir);
    }

    @Override
    public void uploadFile(String localPath, String remotePath) throws SftpException {
        sftpOperations.uploadFile(localPath, remotePath);
    }

    @Override
    public void downloadFile(String remotePath, String localPath) throws SftpException {
        sftpOperations.downloadFile(remotePath, localPath);
    }

    @Override
    public void deleteFile(String remoteFile) throws SftpException {
        sftpOperations.deleteFile(remoteFile);
    }

    @Override
    public void renameFile(String oldPath, String newPath) throws SftpException {
        sftpOperations.renameFile(oldPath, newPath);
    }

    @Override
    public void createDirectory(String remoteDir) throws SftpException {
        sftpOperations.createDirectory(remoteDir);
    }

    @Override
    public void deleteDirectory(String remoteDir) throws SftpException {
        sftpOperations.deleteDirectory(remoteDir);
    }

    @Override
    public void chmod(int permissions, String remotePath) throws SftpException {
        sftpOperations.chmod(permissions, remotePath);
    }

    @Override
    public boolean exists(String remotePath) throws SftpException {
        return sftpOperations.exists(remotePath);
    }

    @Override
    public long getFileSize(String remotePath) throws SftpException {
        return sftpOperations.getFileSize(remotePath);
    }

    @Override
    public long getFileModificationTime(String remotePath) throws SftpException {
        return sftpOperations.getFileModificationTime(remotePath);
    }

    @Override
    public String getFilePermissions(String remotePath) throws SftpException {
        return sftpOperations.getFilePermissions(remotePath);
    }

    @Override
    public void setFileModificationTime(String remotePath, int mtime) throws SftpException {
        sftpOperations.setFileModificationTime(remotePath, mtime);
    }

    @Override
    public void copyFileOrDirectory(String srcPath, String destPath) throws SftpException, IOException {
        sftpOperations.copyFileOrDirectory(srcPath, destPath);
    }

    @Override
    public String getFileType(String remotePath) throws SftpException {
        return sftpOperations.getFileType(remotePath);
    }

    @Override
    public String getFileOwnerAndGroup(String remotePath) throws SftpException {
        return sftpOperations.getFileOwnerAndGroup(remotePath);
    }

    @Override
    public List<String> getFilesAfterTime(String remoteDir, long time) throws SftpException {
        return sftpOperations.getFilesAfterTime(remoteDir, time);
    }

    @Override
    public void moveFile(String srcPath, String destPath) throws SftpException {
        sftpOperations.moveFile(srcPath, destPath);
    }
}
