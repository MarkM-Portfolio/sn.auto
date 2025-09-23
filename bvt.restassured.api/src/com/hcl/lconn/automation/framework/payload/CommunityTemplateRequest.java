package com.hcl.lconn.automation.framework.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommunityTemplateRequest extends BasePayload {
	
	@JsonProperty(value = "handle")
	protected String handle;
	
	@JsonProperty(value = "name")
	protected String name;

	public String getHandle() {
		return handle;
	}

	public CommunityTemplateRequest setHandle(String handle) {
		this.handle = handle;
		return this;
	}

	public String getName() {
		return name;
	}

	public CommunityTemplateRequest setName(String name) {
		this.name = name;
		return this;
	}
	
}
