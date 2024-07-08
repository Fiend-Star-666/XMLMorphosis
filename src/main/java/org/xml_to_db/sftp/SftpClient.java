package org.xml_to_db.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SftpClient implements AutoCloseable {
    private final SftpConfig config;
    private final JSch jsch;
    private ChannelSftp channel;
    private Session session;

    public SftpClient(SftpConfig config) {
        this.config = config;
        this.jsch = new JSch();
    }

    public void connect() throws JSchException {
        log.info("Connecting to SFTP server {}:{}", config.getHost(), config.getPort());
        try {
            session = jsch.getSession(config.getUsername(), config.getHost(), config.getPort());
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(config.getPassword());
            session.connect(30000); // 30 seconds timeout
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect(30000); // 30 seconds timeout
            log.info("Connected successfully to SFTP server");
        } catch (JSchException e) {
            log.error("Failed to connect to SFTP server: {}", e.getMessage());
            throw e;
        }
    }

    public ChannelSftp getChannel() {
        if (channel == null || !channel.isConnected()) {
            throw new IllegalStateException("SFTP channel is not connected");
        }
        return channel;
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.exit();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
}
