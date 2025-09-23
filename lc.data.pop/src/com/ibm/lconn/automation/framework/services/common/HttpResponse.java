package com.ibm.lconn.automation.framework.services.common;

import java.util.Locale;

import org.apache.commons.httpclient.StatusLine;
import org.apache.http.HttpEntity;



public class HttpResponse {
	
	HttpEntity _entity;
	Locale _locale;
	StatusLine _statusline;
	String _reason;
	String _responseBody;
	int _statusCode;
	
	public int getStatusCode() {
		return _statusCode;
	}

	//Obtains the message entity of this response, if any.
	public HttpEntity 	getEntity(){
		return _entity;
	}
	
	//Obtains the locale of this response.
	public Locale 	getLocale() { 
		return _locale;
	}	
	
	//Obtains the status line of this response.
	public StatusLine getStatusLine()	{
		return _statusline;
	}
	
	//Associates a response entity with this response.
	public void setEntity(HttpEntity entity){
		_entity = entity;
	}	
	
	//Changes the locale of this response.
	public void setLocale(Locale loc){
		_locale = loc;
	}
	
	
	//Updates the status line of this response with a new reason phrase.
	public void setReasonPhrase(String reason){
		_reason = reason;
	}
	
	//Updates the status line of this response with a new status code.
	public void setStatusCode(int code){
		_statusCode = code;
	}
	
	//Sets the status line of this response.
	public void setStatusLine(StatusLine statusline){
		_statusline = statusline;
	}
	
	public String getResponseBody() {
		return _responseBody;
	}

	public void setResponseBody(String responseBody) {
		this._responseBody = responseBody;
	}
	
	/*//Sets the status line of this response.
	void 	setStatusLine(ProtocolVersion ver, int code){
		
	}
	
	//Sets the status line of this response with a reason phrase.
	void 	setStatusLine(ProtocolVersion ver, int code, String reason){
		
	}*/
     
}
