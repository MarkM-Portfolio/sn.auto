package com.hcl.lconn.automation.framework.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminBannerResponse extends BasePayload {
	
	@JsonProperty(value = "severity")
	protected String severity;
	
	@JsonProperty(value = "message")
	protected List<String> message;
	
	@JsonProperty(value = "open")
	protected boolean open;
	
	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public List<String> getMessage() {
		return message;
	}

	public void setMessage(List<String> message) {
		this.message = message;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
	
}
