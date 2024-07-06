package org.xmlToDb.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DatabaseConfiguration {
    private final String url;
    private final String username;
    private final String password;
    private final String driverClassName;
}