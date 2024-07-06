package org.xml_to_db.utils;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class GlobalExceptionHandler {
    public static HttpResponseMessage handleException(HttpRequestMessage<Optional<String>> request, Exception e) {
        // Log the exception
        log.error("Exception caught: " + e.getMessage());
        e.printStackTrace();
        // Return an error response
        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + e.getMessage())
                .build();
    }

    private GlobalExceptionHandler() {
    }
}
