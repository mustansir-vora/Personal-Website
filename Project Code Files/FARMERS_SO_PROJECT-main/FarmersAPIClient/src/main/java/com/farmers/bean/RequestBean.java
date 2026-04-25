package com.farmers.bean;

import java.util.Map;

public class RequestBean {

	private String url;
    private String requestBody;
    private String methodType;
    private Map<String, String> headers;
    
    public RequestBean(String url, String requestBody, String methodType,
            Map<String, String> headers) {
    	this.url = url;
        this.requestBody = requestBody;
        this.methodType = methodType;
        this.headers = headers;
    }
    
    public String getUrl() {
        return url;
    }

    public String getRequestBody() {
        return requestBody;
    }
    
    public String getMethodType() {
        return methodType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
