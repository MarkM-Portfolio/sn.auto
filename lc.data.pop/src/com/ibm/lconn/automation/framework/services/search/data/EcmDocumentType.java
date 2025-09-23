package com.ibm.lconn.automation.framework.services.search.data;


public class EcmDocumentType {

	String id;
	String label;

	public EcmDocumentType(String id, String label) {
		super();
		this.id = id;
		this.label = label;
	}
	
	public String getId() {
		return id;
	}
	public void setDd(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "EcmDocumentType [id=" + id
				+ ", label=" + label + "]";
	}
	
	
}
