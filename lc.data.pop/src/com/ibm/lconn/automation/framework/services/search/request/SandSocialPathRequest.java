package com.ibm.lconn.automation.framework.services.search.request;

import com.ibm.lconn.automation.framework.services.search.data.ConnectionType;
import com.ibm.lconn.automation.framework.services.search.data.Person;


public class SandSocialPathRequest extends BaseSearchRequest{
	
	private Person targetPerson = null;
	private Person sourcePerson = null;
	private Integer maxLenght = null;
	private ConnectionType connectionType = null;
	
	public SandSocialPathRequest()  {
		super();
	}
	
	public Person getTargetPerson() {
		return targetPerson;
	}

	public void setTargetPerson(Person target) {
		this.targetPerson = target;
	}

	public Person getSourcePerson() {
		return sourcePerson;
	}

	public void setSourcePerson(Person sourcePerson) {
		this.sourcePerson = sourcePerson;
	}
	
	public Integer getMaxLenght() {
		return maxLenght;
	}

	public void setMaxLenght(Integer maxLenght) {
		this.maxLenght = maxLenght;
	}
	
	public ConnectionType getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(ConnectionType connectionType) {
		this.connectionType = connectionType;
	}

	@Override
	public String toString() {
		return "SandSocialPathRequest [targetPerson=" + targetPerson
				+ ", sourcePerson=" + sourcePerson + ", maxLenght=" + maxLenght
				+ ", connectionType=" + connectionType + ", contextPath="
				+ getContextPath() + "]";
	}

	public String getContextPathString() {
		
		return "/" +getContextPath().toString();
	}
	
	
}

