package org.xmlToDb.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.xmlToDb.services.XmlProcessingService;

public class HttpTriggerFunction {
    @FunctionName("HttpTriggerFunction")
    public HttpResponseMessage processRequest(
            @HttpTrigger(name = "processXml", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION, route = "/getAndParseXml") HttpRequestMessage<String> request,
            final ExecutionContext context) {

        String xmlContent = request.getBody();
        if (xmlContent == null) {
            xmlContent = "";
        }

        XmlProcessingService service = new XmlProcessingService();
        service.processXml(xmlContent);

        return request.createResponseBuilder(HttpStatus.OK).body("Processing Started").build();
    }
}
