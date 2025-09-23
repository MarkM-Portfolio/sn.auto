package com.ibm.lconn.automation.framework.services.common.nodes;

import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

public class FollowEntry extends LCEntry {

	private String _source;
	private String _resourceId;
	private String _resourceType;
	
	
	public FollowEntry(String source, String resourceId, String resourceType) {
		super();
		_source = source;
		_resourceId = resourceId;
		_resourceType = resourceType;
	}


	
	@Override
	public Entry toEntry() {
		Entry result =  super.toEntry();
		
		result.addCategory(StringConstants.SCHEME_SOURCE, _source, null);
		result.addCategory(StringConstants.SCHEME_RESOURCE_TYPE, _resourceType, null);
		result.addCategory(StringConstants.SCHEME_RESOURCE_ID, _resourceId, null);
		
		return result;
	}



	public String getSource() {
		return _source;
	}



	public void setSource(String source) {
		_source = source;
	}



	public String getResourceId() {
		return _resourceId;
	}


	public void setResourceId(String resourceId) {
		_resourceId = resourceId;
	}


	public String getResourceType() {
		return _resourceType;
	}

	public void setResourceType(String resourceType) {
		_resourceType = resourceType;
	}
}
