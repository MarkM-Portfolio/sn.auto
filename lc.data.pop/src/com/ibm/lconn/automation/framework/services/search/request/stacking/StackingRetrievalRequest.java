package com.ibm.lconn.automation.framework.services.search.request.stacking;

public class StackingRetrievalRequest {

	private Integer numOfStacks = null;
	private Boolean shouldReturnDebugFields = false;
	
	public StackingRetrievalRequest(Integer numOfStacks, Boolean shouldReturnDebugFields) {
		this.numOfStacks = numOfStacks;
		this.shouldReturnDebugFields = shouldReturnDebugFields;
	}
	
	public int getNumOfStacks() {
		return numOfStacks;
	}

	public void setNumOfStacks(Integer numOfStacks) {
		this.numOfStacks = numOfStacks;
	}

	
	public Boolean shouldReturnDebugFields() {
		return shouldReturnDebugFields;
	}

	public void setShouldReturnDebugFields(Boolean shouldReturnDebugFields) {
		this.shouldReturnDebugFields = shouldReturnDebugFields;
	}

	@Override
	public String toString() {
		StringBuilder params = new StringBuilder();
		if ( numOfStacks!=null ){
			params.append("&").append("numOfStacks").append("=").append(numOfStacks);
		}
		if(shouldReturnDebugFields!= false){
			params.append("&").append("shouldReturnDebugFields").append("=").append(shouldReturnDebugFields);
		}
		return params.toString();
	}
}