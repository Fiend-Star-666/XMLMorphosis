# XML to DB Processing System Documentation

## Table of Contents
1. [System Overview](#system-overview)
2. [Component Descriptions](#component-descriptions)
3. [Configuration Guide](#configuration-guide)
4. [Usage Instructions](#usage-instructions)
5. [Extension Points](#extension-points)
6. [Troubleshooting](#troubleshooting)

## 1. System Overview

The XML to DB Processing System is a flexible and extensible solution designed to process XML files, validate them against XSD schemas, and store the processed data in various database systems. The system is built as an Azure Function (with potential for AWS Lambda adaptation) and supports multiple storage providers and database types.

Key features:
- Support for multiple cloud storage providers (Azure Blob Storage, AWS S3)
- Dynamic XML processing based on XML/XSD pairs
- Flexible database connections for different XML types
- Extensible architecture for adding new processors and database connections

## 2. Component Descriptions

### 2.1 HttpTriggerFunction
The main entry point for the system, triggered by HTTP requests. It orchestrates the XML processing workflow.

### 2.2 StorageService
An interface defining operations for file storage. Implementations include:
- **AzureBlobStorageService**: Handles file operations in Azure Blob Storage
- **S3StorageService**: Handles file operations in AWS S3

### 2.3 XMLProcessor
An interface for processing XML documents. Custom processors can be implemented for different XML types.

### 2.4 DatabaseConnection
Manages database connections and operations. Supports multiple database types through configuration.

### 2.5 ConfigLoader
Loads and manages application configuration from the `application.properties` file and environment variables.

### 2.6 XMLValidator
Provides XML validation against XSD schemas and XML parsing functionality.

### 2.7 GlobalExceptionHandler
Centralizes exception handling for the entire application.

## 3. Configuration Guide

### 3.1 application.properties

```properties
# Storage configuration
STORAGE_TYPE=azure
AZURE_STORAGE_CONNECTION_STRING=your_azure_connection_string
AZURE_STORAGE_CONTAINER_NAME=your_container_name
AWS_S3_BUCKET_NAME=your_s3_bucket_name

# XML Processor configuration
XML_PROCESSOR_CLASSES=org.xmlToDb.processors.TypeAXMLProcessor,org.xmlToDb.processors.TypeBXMLProcessor

# Database configurations
DB_CONFIGURATIONS=db1,db2

# DB1 Configuration
DB_URL_db1=jdbc:mysql://localhost:3306/database1
DB_USERNAME_db1=user1
DB_PASSWORD_db1=pass1
DB_DRIVER_db1=com.mysql.cj.jdbc.Driver

# DB2 Configuration
DB_URL_db2=jdbc:postgresql://localhost:5432/database2
DB_USERNAME_db2=user2
DB_PASSWORD_db2=pass2
DB_DRIVER_db2=org.postgresql.Driver

# XML to DB mappings
XML_DB_MAPPING=fileA.xml:schema1.xsd:db1,fileB.xml:schema2.xsd:db2

# Other configuration
XML_SCHEMA_PATH=/schemas/
```

### 3.2 Environment Variables
All properties in `application.properties` can be overridden by environment variables. This is particularly useful for sensitive information like database credentials.

## 4. Usage Instructions

### 4.1 Deploying the Function
1. Build the project using Maven: `mvn clean package`
2. Deploy the function to Azure using Azure Functions Core Tools or Azure CLI

### 4.2 Triggering the Process
Send an HTTP POST request to the function's URL. The function will process all XML files in the configured storage location.

### 4.3 Monitoring
Use Azure Application Insights or AWS CloudWatch (depending on deployment) to monitor function execution and logs.

## 5. Extension Points

### 5.1 Adding a New Storage Service
1. Implement the `StorageService` interface
2. Add the new service to `StorageServiceFactory`
3. Update `application.properties` with new storage type

### 5.2 Adding a New XML Processor
1. Implement the `XMLProcessor` interface
2. Add the new processor class name to `XML_PROCESSOR_CLASSES` in `application.properties`

### 5.3 Adding a New Database Type
1. Update `DatabaseConfiguration` and `DatabaseConnection` if necessary
2. Add new database configuration to `application.properties`
3. Update `DatabaseConnectionFactory` to handle the new database type

## 6. Troubleshooting

### 6.1 Common Issues
- **Storage Connection Failures**: Check storage connection strings and permissions
- **XML Validation Errors**: Ensure XML files conform to their XSD schemas
- **Database Connection Issues**: Verify database credentials and network access

### 6.2 Logging
The system uses extensive logging. Check function logs in Azure Portal or AWS CloudWatch for detailed error messages and stack traces.

### 6.3 Support
For further assistance, contact the development team or raise an issue in the project's issue tracker.

