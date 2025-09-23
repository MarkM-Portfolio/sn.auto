
package com.hcl.lconn.automation.framework.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfileUpdateRequest extends BasePayload {
	
	@JsonProperty(value = "jobTitle")
	protected String jobTitle;
	
	@JsonProperty(value = "officeNumber")
	protected String officeNumber;
	
	@JsonProperty(value = "workPhone")
	protected String workPhone;
	
	@JsonProperty(value = "cellPhone")
	protected String cellPhone;
	
	@JsonProperty(value = "faxNumber")
	protected String faxNumber;
	
	@JsonProperty(value = "description")
	protected String description;
	
	@JsonProperty(value = "experience")
	protected String experience;

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getOfficeNumber() {
		return officeNumber;
	}

	public void setOfficeNumber(String officeNumber) {
		this.officeNumber = officeNumber;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}
}