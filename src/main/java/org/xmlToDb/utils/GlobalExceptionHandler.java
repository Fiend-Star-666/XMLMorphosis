package org.xmlToDb.utils;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

import java.util.Optional;

public class GlobalExceptionHandler {

    public static HttpResponseMessage handleException(HttpRequestMessage<Optional<String>> request, Throwable throwable) {
        // Log the exception (can use any logging framework)
        System.err.println("Exception caught: " + throwable.getMessage());
        throwable.printStackTrace();

        // Return a standardized error response
        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + throwable.getMessage())
                .build();
    }
}
