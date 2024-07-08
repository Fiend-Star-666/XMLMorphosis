package org.xml_to_db.sftp;

import lombok.Data;
import org.xml_to_db.config.ConfigLoader;

@Data
public class SftpConfig {
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public SftpConfig(ConfigLoader configLoader) {
        this.host = configLoader.getProperty("sftp.host");
        this.port = Integer.parseInt(configLoader.getProperty("sftp.port"));
        this.username = configLoader.getProperty("sftp.username");
        this.password = configLoader.getProperty("sftp.password");
    }
}
