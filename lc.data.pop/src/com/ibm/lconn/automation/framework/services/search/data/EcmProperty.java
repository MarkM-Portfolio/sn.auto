package com.ibm.lconn.automation.framework.services.search.data;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;


public class EcmProperty {
	
	private static final String PROP_NAME = "name";
	private static final String PROP_LABEL = "label";
	private static final String PROP_DATA_TYPE = "dataType";
	private static final String PROP_MULTIPLE = "multiple";
	private static final String PROP_EXACT_MATCH = "exactMatch";


	String name;
	String label;
	String dataType;
	Boolean multiple;
	Boolean exactMatch;
	
	public EcmProperty(String name, String label, String dataType,
			Boolean multiple, Boolean exactMatch) {
		super();
		this.name = name;
		this.label = label;
		this.dataType = dataType;
		this.multiple = multiple;
		this.exactMatch = exactMatch;
	}
	
	public EcmProperty(JSONObject jsonObject) throws JSONException {
		this.name = jsonObject.getString(PROP_NAME);
		this.label = jsonObject.getString(PROP_LABEL);
		this.dataType = jsonObject.getString(PROP_DATA_TYPE);
		this.multiple = jsonObject.getBoolean(PROP_MULTIPLE);
		this.exactMatch = jsonObject.getBoolean(PROP_EXACT_MATCH);
		
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public Boolean getMultiple() {
		return multiple;
	}
	public void setMultiple(Boolean multiple) {
		this.multiple = multiple;
	}
	public Boolean getExactMatch() {
		return exactMatch;
	}
	public void setExactMatch(Boolean exactMatch) {
		this.exactMatch = exactMatch;
	}

	@Override
	public String toString() {
		return "EcmProperty [name=" + name + ", label=" + label + ", dataType="
				+ dataType + ", multiple=" + multiple + ", exactMatch="
				+ exactMatch + "]";
	}
	

	
	
}
