package org.xml_to_db.storage;

import java.util.List;

public interface StorageService {
    List<String> listFiles(String fileExtension);

    String readFileContent(String filePath);

    void writeFile(String filePath, String content);

    void deleteFile(String filePath);
}
