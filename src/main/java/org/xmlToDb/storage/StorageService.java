package org.xmlToDb.storage;

import java.util.List;

public interface StorageService {
    void storeFile(String filePath);

    String readFileContent(String xmlFilePath);

    List<String> listFiles(String xml);
}
