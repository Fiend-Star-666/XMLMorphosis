//package org.xmlToDb.functions;
//
//import com.microsoft.azure.functions.HttpMethod;
//import com.microsoft.azure.functions.HttpRequestMessage;
//import com.microsoft.azure.functions.HttpResponseMessage;
//import com.microsoft.azure.functions.HttpStatus;
//import com.microsoft.azure.functions.annotation.AuthorizationLevel;
//import com.microsoft.azure.functions.annotation.FunctionName;
//import com.microsoft.azure.functions.annotation.HttpTriggerTest;
//import com.microsoft.azure.functions.sql.annotation.CommandType;
//import com.microsoft.azure.functions.sql.annotation.SQLInput;
//
//import java.util.Optional;
//
//public class SqlInputBindingJava {
//    /**
//     * Visit https://aka.ms/sqlbindingsinput to learn how to use this input binding
//     *
//     * These tasks should be completed prior to running:
//     * 1. Add com.microsoft.azure.functions:azure-functions-java-library-sql to your project dependencies
//     * 2. Add an app setting named "SqlConnectionString" containing the connection string to use for the SQL connection
//     * 3. Change the bundle name in host.json to "Microsoft.Azure.Functions.ExtensionBundle" and the version to "[4.*, 5.0.0)"
//     */
//    @FunctionName("SqlInputBindingJava")
//    public HttpResponseMessage run(
//            @HttpTriggerTest(
//                name = "req",
//                methods = {HttpMethod.GET},
//                authLevel = AuthorizationLevel.FUNCTION,
//                route = "")
//                HttpRequestMessage<Optional<String>> request,
//            @SQLInput(
//                name = "result",
//                commandText = "SELECT * FROM [dbo].[table1]",
//                commandType = CommandType.Text,
//                connectionStringSetting = "<value>")
//                Object[] result) {
//
//        return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(result).build();
//    }
//}
