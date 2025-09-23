package com.hcl.lconn.automation.framework.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommunityTemplateResponse extends BasePayload {
	
	@JsonProperty(value = "description")
	protected String description;
	
	@JsonProperty(value = "name")
	protected String name;
	
	@JsonProperty(value = "templateId")
	protected String templateId;
	
	@JsonProperty(value = "communityUuid")
	protected String communityUuid;

	@JsonProperty(value = "categories")
	protected List<String> categories;
	
	@JsonProperty(value = "createdBy")
	protected String createdBy;
	
	@JsonProperty(value = "createdDate")
	protected String createdDate;
	
	@JsonProperty(value = "lastUpdatedBy")
	protected String lastUpdatedBy;
	
	@JsonProperty(value = "lastUpdatedDate")
	protected String lastUpdatedDate;
	
	@JsonProperty(value = "orgId")
	protected String orgId;
	
	public List<String> getCategories() {
		return categories;
	}

	public CommunityTemplateResponse setCategories(List<String> categories) {
		this.categories = categories;
		return this;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public CommunityTemplateResponse setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public CommunityTemplateResponse setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
		return this;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public CommunityTemplateResponse setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
		return this;
	}

	public String getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public CommunityTemplateResponse setLastUpdatedDate(String lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
		return this;
	}

	public String getOrgId() {
		return orgId;
	}

	public CommunityTemplateResponse setOrgId(String orgId) {
		this.orgId = orgId;
		return this;
	}
	
	public String getDescription() {
		return description;
	}

	public CommunityTemplateResponse setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getName() {
		return name;
	}

	public CommunityTemplateResponse setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getTemplateId() {
		return templateId;
	}

	public CommunityTemplateResponse setTemplateId(String templateId) {
		this.templateId = templateId;
		return this;
	}

	public String getCommunityUuid() {
		return communityUuid;
	}

	public CommunityTemplateResponse setCommunityUuid(String communityUuid) {
		this.communityUuid = communityUuid;
		return this;
	}
	
}
