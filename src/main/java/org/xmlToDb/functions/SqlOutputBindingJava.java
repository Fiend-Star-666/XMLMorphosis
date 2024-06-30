//package org.xmlToDb.functions;
//
//import com.google.gson.Gson;
//import com.microsoft.azure.functions.HttpMethod;
//import com.microsoft.azure.functions.HttpRequestMessage;
//import com.microsoft.azure.functions.HttpResponseMessage;
//import com.microsoft.azure.functions.HttpStatus;
//import com.microsoft.azure.functions.OutputBinding;
//import com.microsoft.azure.functions.annotation.AuthorizationLevel;
//import com.microsoft.azure.functions.annotation.FunctionName;
//import com.microsoft.azure.functions.annotation.HttpTriggerTest;
//import com.microsoft.azure.functions.sql.annotation.SQLOutput;
//
//import java.io.IOException;
//import java.util.Optional;
//
//public class SqlOutputBindingJava {
//    /**
//     * Visit Visit https://aka.ms/sqlbindingsoutput to learn how to use this output binding
//     *
//     * These tasks should be completed prior to running:
//     * 1. Add com.microsoft.azure.functions:azure-functions-java-library-sql and com.google.code.gson to your project dependencies
//     * 2. Add an app setting named \"SqlConnectionString\" containing the connection string to use for the SQL connection
//     * 3. Change the bundle name in host.json to \"Microsoft.Azure.Functions.ExtensionBundle\" and the version to \"[4.*, 5.0.0)\"
//     */
//    @FunctionName("SqlOutputBindingJava")
//    public HttpResponseMessage run(
//            @HttpTriggerTest(
//                name = "req",
//                methods = {HttpMethod.POST},
//                authLevel = AuthorizationLevel.FUNCTION,
//                route = "")
//                HttpRequestMessage<Optional<String>> request,
//            @SQLOutput(
//                name = "output",
//                commandText = "[dbo].[table1]",
//                connectionStringSetting = "<value>")
//                OutputBinding<TodoItem> output) throws IOException {
//
//        String json = request.getBody().get();
//        Gson gson = new Gson();
//        TodoItem todoItem = gson.fromJson(json, TodoItem.class);
//        output.setValue(todoItem);
//
//        return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(todoItem).build();
//    }
//
//    public static class TodoItem {
//        public String Id;
//        public int Priority;
//        public String Description;
//
//        public String getId() {
//            return Id;
//        }
//
//        public void setId(String id) {
//            Id = id;
//        }
//
//        public int getPriority() {
//            return Priority;
//        }
//
//        public void setPriority(int priority) {
//            Priority = priority;
//        }
//
//        public String getDescription() {
//            return Description;
//        }
//
//        public void setDescription(String description) {
//            Description = description;
//        }
//    }
//}