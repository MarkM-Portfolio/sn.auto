package com.hcl.lconn.automation.framework.utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class CustomRequestFilter implements Filter {
	public static FilterableRequestSpecification requestSpecification;
	public static Response response;

    @Override
    public synchronized Response filter(FilterableRequestSpecification reqSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        
    	response = ctx.next(reqSpec, responseSpec);
    	requestSpecification=reqSpec;
        return response;
    }

	public synchronized static String publishResponse(){
		FilterableRequestSpecification requestSpec = requestSpecification;
		return "Request method : " + requestSpec.getMethod() + 
		  		"<br>Request URI :  " + requestSpec.getURI() +  
		  		"<br>Request Body : " + requestSpec.getBody() +
		  		"<br>Request Proxy : " + requestSpec.getProxySpecification() + 
		  		"<br>Request Params : " + requestSpec.getRequestParams() + 
		  		"<br>Query Params : " + requestSpec.getQueryParams() +
		  		"<br>Form Params : " + requestSpec.getFormParams() +
		  		"<br>Path Params : " + requestSpec.getPathParams() +
		  		"<br>Headers : " + requestSpec.getHeaders() +
		  		//"<br>Cookies : " + requestSpec.getCookies() +   commented because it returns a long string
		  		"<br>Multiparts : " + requestSpec.getMultiPartParams() +
		  		"<br>Response Status :  " + response.getStatusCode() + " " + response.getStatusLine() + 
		        "<br>Response Body :  " + response.getBody().prettyPrint() +  
		        "<br>Response Time Taken :  " + response.getTime() + " milliseconds" +
		        "<br>Content-Type :  " + response.getContentType();
	}
    
    
}