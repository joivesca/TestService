package com.onndoo.ws;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

@WebService
public class CalculatorWebServiceImpl implements CalculatorWebService {
    
	@Resource
    private WebServiceContext wsContext;
	
	
	@WebMethod
    public int add(int num1, int num2) {
		if (!isAuthenticated()) {
            throw new SecurityException("Unauthorized access");
        }
        return num1 + num2;
    }
    
    
    private boolean isAuthenticated() {
        MessageContext messageContext = wsContext.getMessageContext();
        Map<String, List<String>> headers = (Map<String, List<String>>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);

        if (headers == null || !headers.containsKey("Authorization")) {
            return false;
        }

        List<String> authHeaderList = headers.get("Authorization");
        if (authHeaderList == null || authHeaderList.isEmpty()) {
            return false;
        }

        String authHeader = authHeaderList.get(0);
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String encodedCredentials = authHeader.substring(6);
            String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials));
            String[] parts = decodedCredentials.split(":");

            if (parts.length != 2) {
                return false;
            }

            String username = parts[0];
            String password = parts[1];

            return "admin".equals(username) && "password123".equals(password); // Replace this with secure validation
        }
        return false;
    }
}
