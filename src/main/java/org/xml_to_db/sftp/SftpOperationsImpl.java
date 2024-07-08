package org.xml_to_db.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Slf4j
public class SftpOperationsImpl implements SftpOperations {
    private final SftpClient sftpClient;

    public SftpOperationsImpl(SftpClient sftpClient) {
        this.sftpClient = sftpClient;
    }

    @Override
    public void listFiles(String remoteDir) throws SftpException {
        log.info("Listing [{}]...", remoteDir);
        ChannelSftp channel = sftpClient.getChannel();
        channel.cd(remoteDir);
        Vector<ChannelSftp.LsEntry> files = channel.ls(".");
        for (ChannelSftp.LsEntry file : files) {
            log.info("File: {}", file.toString());
            String name = file.getFilename();
            SftpATTRS attrs = file.getAttrs();
            String permissions = attrs.getPermissionsString();
            String size = SftpUtil.humanReadableByteCount(attrs.getSize());
            if (attrs.isDir()) {
                size = "PRE";
            }
            log.info("[{}] {}({})", permissions, name, size);
        }
    }

    @Override
    public void uploadFile(String localPath, String remotePath) throws SftpException {
        log.info("Uploading [{}] to [{}]...", localPath, remotePath);
        sftpClient.getChannel().put(localPath, remotePath);
    }

    @Override
    public void downloadFile(String remotePath, String localPath) throws SftpException {
        log.info("Downloading [{}] to [{}]...", remotePath, localPath);
        sftpClient.getChannel().get(remotePath, localPath);
    }

    @Override
    public void deleteFile(String remoteFile) throws SftpException {
        log.info("Deleting [{}]...", remoteFile);
        sftpClient.getChannel().rm(remoteFile);
    }

    @Override
    public void renameFile(String oldPath, String newPath) throws SftpException {
        log.info("Renaming [{}] to [{}]...", oldPath, newPath);
        sftpClient.getChannel().rename(oldPath, newPath);
    }

    @Override
    public void createDirectory(String remoteDir) throws SftpException {
        log.info("Creating directory [{}]...", remoteDir);
        sftpClient.getChannel().mkdir(remoteDir);
    }

    @Override
    public void deleteDirectory(String remoteDir) throws SftpException {
        log.info("Deleting directory [{}]...", remoteDir);
        sftpClient.getChannel().rmdir(remoteDir);
    }

    @Override
    public void chmod(int permissions, String remotePath) throws SftpException {
        log.info("Changing permissions of [{}] to [{}]...", remotePath, permissions);
        sftpClient.getChannel().chmod(permissions, remotePath);
    }

    @Override
    public boolean exists(String remotePath) throws SftpException {
        log.info("Checking if [{}] exists...", remotePath);
        try {
            sftpClient.getChannel().lstat(remotePath);
            return true;
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public long getFileSize(String remotePath) throws SftpException {
        log.info("Getting size of [{}]...", remotePath);
        return sftpClient.getChannel().lstat(remotePath).getSize();
    }

    @Override
    public long getFileModificationTime(String remotePath) throws SftpException {
        log.info("Getting modification time of [{}]...", remotePath);
        return sftpClient.getChannel().lstat(remotePath).getMTime();
    }

    @Override
    public String getFilePermissions(String remotePath) throws SftpException {
        log.info("Getting permissions of [{}]...", remotePath);
        return sftpClient.getChannel().lstat(remotePath).getPermissionsString();
    }

    @Override
    public void setFileModificationTime(String remotePath, int mtime) throws SftpException {
        log.info("Setting modification time of [{}] to [{}]...", remotePath, mtime);
        SftpATTRS attrs = sftpClient.getChannel().lstat(remotePath);
        attrs.setACMODTIME(attrs.getATime(), mtime);
        sftpClient.getChannel().setStat(remotePath, attrs);
    }

    @Override
    public void copyFileOrDirectory(String srcPath, String destPath) throws SftpException, IOException {
        log.info("Copying [{}] to [{}]...", srcPath, destPath);
        File tempFile = File.createTempFile("sftp", null);
        tempFile.deleteOnExit();
        String tempFilePath = tempFile.getAbsolutePath();

        sftpClient.getChannel().get(srcPath, tempFilePath);
        sftpClient.getChannel().put(tempFilePath, destPath);
    }

    @Override
    public String getFileType(String remotePath) throws SftpException {
        log.info("Getting type of [{}]...", remotePath);
        SftpATTRS attrs = sftpClient.getChannel().lstat(remotePath);
        return attrs.isDir() ? "Directory" : "File";
    }

    @Override
    public String getFileOwnerAndGroup(String remotePath) throws SftpException {
        log.info("Getting owner and group of [{}]...", remotePath);
        SftpATTRS attrs = sftpClient.getChannel().lstat(remotePath);
        return attrs.getUId() + ":" + attrs.getGId();
    }

    @Override
    public List<String> getFilesAfterTime(String remoteDir, long time) throws SftpException {
        log.info("Getting files in [{}] modified after [{}]...", remoteDir, time);
        Vector<ChannelSftp.LsEntry> files = sftpClient.getChannel().ls(remoteDir);
        List<String> filteredFiles = new ArrayList<>();
        for (ChannelSftp.LsEntry file : files) {
            SftpATTRS attrs = file.getAttrs();
            if (attrs.getMTime() > time) {
                filteredFiles.add(file.getFilename());
            }
        }
        return filteredFiles;
    }

    @Override
    public void moveFile(String srcPath, String destPath) throws SftpException {
        log.info("Moving [{}] to [{}]...", srcPath, destPath);
        sftpClient.getChannel().rename(srcPath, destPath);
    }
}
