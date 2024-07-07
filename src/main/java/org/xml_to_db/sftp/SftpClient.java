package org.xml_to_db.sftp;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.xml_to_db.config.ConfigLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

@Slf4j
public final class SftpClient {
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final JSch jsch;
    ConfigLoader config = ConfigLoader.getInstance();
    private ChannelSftp channel;
    private Session session;


    public SftpClient() {
        log.info("Initializing SFTP client...");
        this.host = config.getProperty("sftp.host");
        this.port = Integer.parseInt(config.getProperty("sftp.port"));
        this.username = config.getProperty("sftp.username");
        this.password = config.getProperty("sftp.password");
        this.jsch = new JSch();
    }

    /**
     * Authenticate with remote using password
     *
     * @throws JSchException If there is problem with credentials or connection
     */
    public void authPassword() throws JSchException {
        session = jsch.getSession(username, host, port);
        //disable known hosts checking
        //if you want to set knows hosts file You can set with jsch.setKnownHosts("path to known hosts file");
        var config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(password);
        session.connect();
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
    }


    public void authKey(String keyPath, String pass) throws JSchException {
        jsch.addIdentity(keyPath, pass);
        session = jsch.getSession(username, host, port);
        //disable known hosts checking
        //if you want to set knows hosts file You can set with jsch.setKnownHosts("path to known hosts file");
        var config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
    }

    /**
     * List all files including directories
     *
     * @param remoteDir Directory on remote from which files will be listed
     * @throws SftpException If there is any problem with listing files related to permissions etc
     * @throws JSchException If there is any problem with connection
     */
    @SuppressWarnings("unchecked")
    public void listFiles(String remoteDir) throws SftpException, JSchException {
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        log.info("Listing [%s]...%n", remoteDir);
        channel.cd(remoteDir);
        Vector<ChannelSftp.LsEntry> files = channel.ls(".");
        for (ChannelSftp.LsEntry file : files) {
            log.info("File: {}", file.toString());
            var name = file.getFilename();
            var attrs = file.getAttrs();
            var permissions = attrs.getPermissionsString();
            var size = SftpUtil.humanReadableByteCount(attrs.getSize());
            if (attrs.isDir()) {
                size = "PRE";
            }
            log.info("[%s] %s(%s)%n", permissions, name, size);
        }
    }

    /**
     * Upload a file to remote
     *
     * @param localPath  full path of location file
     * @param remotePath full path of remote file
     * @throws JSchException If there is any problem with connection
     * @throws SftpException If there is any problem with uploading file permissions etc
     */
    public void uploadFile(String localPath, String remotePath) throws JSchException, SftpException {
        log.info("Uploading [%s] to [%s]...%n", localPath, remotePath);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.put(localPath, remotePath);
    }

    /**
     * Download a file from remote
     *
     * @param remotePath full path of remote file
     * @param localPath  full path of where to save file locally
     * @throws SftpException If there is any problem with downloading file related permissions etc
     */
    public void downloadFile(String remotePath, String localPath) throws SftpException {
        log.info("Downloading [%s] to [%s]...%n", remotePath, localPath);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.get(remotePath, localPath);
    }

    /**
     * Delete a file on remote
     *
     * @param remoteFile full path of remote file
     * @throws SftpException If there is any problem with deleting file related to permissions etc
     */
    public void delete(String remoteFile) throws SftpException {
        log.info("Deleting [%s]...%n", remoteFile);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.rm(remoteFile);
    }

    /**
     * Disconnect from remote
     */
    public void close() {
        if (channel != null) {
            channel.exit();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    /**
     * Rename a file on remote
     *
     * @param oldPath
     * @param newPath
     * @throws SftpException
     */
    public void renameFile(String oldPath, String newPath) throws SftpException {
        log.info("Renaming [%s] to [%s]...%n", oldPath, newPath);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.rename(oldPath, newPath);
    }

    /**
     * Create a directory on remote
     *
     * @param remoteDir
     * @throws SftpException
     */
    public void createDirectory(String remoteDir) throws SftpException {
        log.info("Creating directory [%s]...%n", remoteDir);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.mkdir(remoteDir);
    }

    /**
     * Delete a directory on remote
     *
     * @param remoteDir
     * @throws SftpException
     */
    public void deleteDirectory(String remoteDir) throws SftpException {
        log.info("Deleting directory [%s]...%n", remoteDir);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.rmdir(remoteDir);
    }

    /**
     * Change permissions of a file or directory on remote
     *
     * @param permissions
     * @param remotePath
     * @throws SftpException
     */
    public void chmod(int permissions, String remotePath) throws SftpException {
        log.info("Changing permissions of [%s] to [%d]...%n", remotePath, permissions);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.chmod(permissions, remotePath);
    }

    /**
     * Check if a file or directory exists on remote
     *
     * @param remotePath
     * @return
     * @throws SftpException
     */
    public boolean exists(String remotePath) throws SftpException {
        log.info("Checking if [%s] exists...%n", remotePath);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        try {
            channel.ls(remotePath);
            return true;
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
            throw e;
        }
    }

    /**
     * Get the size of a file on remote
     *
     * @param remotePath
     * @return
     * @throws SftpException
     */
    public long getFileSize(String remotePath) throws SftpException {
        log.info("Getting size of [%s]...%n", remotePath);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        return channel.lstat(remotePath).getSize();
    }

    /**
     * Get the modification time of a file on remote
     *
     * @param remotePath
     * @return
     * @throws SftpException
     */
    public long getFileModificationTime(String remotePath) throws SftpException {
        System.out.printf("Getting modification time of [%s]...%n", remotePath);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        return channel.lstat(remotePath).getMTime();
    }

    /**
     * Get the permissions of a file on remote
     *
     * @param remotePath
     * @return
     * @throws SftpException
     */
    public String getFilePermissions(String remotePath) throws SftpException {
        System.out.printf("Getting permissions of [%s]...%n", remotePath);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        return channel.lstat(remotePath).getPermissionsString();
    }

    /**
     * Set the modification time of a file on remote
     *
     * @param remotePath
     * @param mtime
     * @throws SftpException
     */
    public void setFileModificationTime(String remotePath, int mtime) throws SftpException {
        System.out.printf("Setting modification time of [%s] to [%d]...%n", remotePath, mtime);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        SftpATTRS attrs = channel.lstat(remotePath);
        attrs.setACMODTIME(attrs.getATime(), mtime);
        channel.setStat(remotePath, attrs);
    }

    /**
     * Copy a file or directory on remote
     *
     * @param srcPath
     * @param destPath
     * @throws SftpException
     * @throws IOException
     */
    public void copyFileOrDirectory(String srcPath, String destPath) throws SftpException, IOException {
        System.out.printf("Copying [%s] to [%s]...%n", srcPath, destPath);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }

        // Create a temporary file
        File tempFile = File.createTempFile("sftp", null);
        tempFile.deleteOnExit();
        String tempFilePath = tempFile.getAbsolutePath();

        // Download the file from the source path to the temporary file
        channel.get(srcPath, tempFilePath);

        // Upload the file from the temporary file to the destination path
        channel.put(tempFilePath, destPath);
    }

    /**
     * Get the type of a file on remote
     *
     * @param remotePath
     * @return
     * @throws SftpException
     */
    public String getFileType(String remotePath) throws SftpException {
        System.out.printf("Getting type of [%s]...%n", remotePath);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        SftpATTRS attrs = channel.lstat(remotePath);
        return attrs.isDir() ? "Directory" : "File";
    }

    /**
     * Get the owner and group of a file on remote
     *
     * @param remotePath
     * @return
     * @throws SftpException
     */
    public String getFileOwnerAndGroup(String remotePath) throws SftpException {
        System.out.printf("Getting owner and group of [%s]...%n", remotePath);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        SftpATTRS attrs = channel.lstat(remotePath);
        return attrs.getUId() + ":" + attrs.getGId();
    }

    /**
     * Get a list of files in a directory on remote modified after a certain time
     *
     * @param remoteDir
     * @param time
     * @return
     * @throws SftpException
     */
    public List<String> getFilesAfterTime(String remoteDir, long time) throws SftpException {
        System.out.printf("Getting files in [%s] modified after [%d]...%n", remoteDir, time);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        Vector<ChannelSftp.LsEntry> files = channel.ls(remoteDir);
        List<String> filteredFiles = new ArrayList<>();
        for (ChannelSftp.LsEntry file : files) {
            SftpATTRS attrs = file.getAttrs();
            if (attrs.getMTime() > time) {
                filteredFiles.add(file.getFilename());
            }
        }
        return filteredFiles;
    }

    /**
     * Move a file on remote
     *
     * @param srcPath
     * @param destPath
     * @throws SftpException
     */
    public void moveFile(String srcPath, String destPath) throws SftpException {
        System.out.printf("Moving [%s] to [%s]...%n", srcPath, destPath);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.rename(srcPath, destPath);
    }
}
