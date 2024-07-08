package org.xml_to_db.sftp;

public interface SftpService extends SftpOperations {
    void connect() throws Exception;

    void disconnect();
}
