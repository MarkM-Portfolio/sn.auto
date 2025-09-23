
package com.ibm.lconn.automation.framework.services.search.request;

import com.ibm.lconn.automation.framework.services.search.data.PeopleFinderAdditionalField;
import com.ibm.lconn.automation.framework.services.search.service.PeopleFinderService.UserType;

public class PeopleFinderRequest {

	private String query;
	private Integer pageSize = 10;
	private Integer page = 1;
	private Integer startIndex = null;
	private Integer count = null;
	private Boolean boostFriends = true;
	private Boolean boostFriendsOfFriends = true;
	private Boolean disablePhonetics = true;
	private Boolean highlight = true;
	private Boolean searchOnlyNameAndEmail = false;
	private Boolean mustMatchNameOrEmail = false;
	private UserType userType = UserType.EMPLOYEE;
	private PeopleFinderAdditionalField additionalFields = null;
	
	public PeopleFinderRequest(String query) {
		this.query = query;
	}

	public int getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Boolean isBoostFriends() {
		return boostFriends;
	}

	public void setBoostFriends(Boolean boostFriends) {
		this.boostFriends = boostFriends;
	}

	public boolean isBoostFriendsOfFriends() {
		return boostFriendsOfFriends;
	}

	public void setBoostFriendsOfFriends(Boolean boostFOF) {
		this.boostFriendsOfFriends = boostFOF;
	}

	public Boolean isDisablePhonetics() {
		return disablePhonetics;
	}

	public void setDisablePhonetics(Boolean applayPhonetics) {
		this.disablePhonetics = applayPhonetics;
	}

	public Boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(Boolean highlight) {
		this.highlight = highlight;
	}
	
	public void setSearchOnlyNameAndEmail(Boolean searchOnlyNameAndEmail) {
		this.searchOnlyNameAndEmail = searchOnlyNameAndEmail;
	}

	public boolean isSearchOnlyNameAndEmail() {
		return searchOnlyNameAndEmail;
	}

	public void setMustMatchNameOrEmail(Boolean mustMatchNameOrEmail) {
		this.mustMatchNameOrEmail = mustMatchNameOrEmail;
	}

	public boolean isMustMatchNameOrEmail() {
		return mustMatchNameOrEmail;
	}

	public String getQuery() {
		return query;
	}
	
	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		if (userType == null){return;}
		this.userType = userType;
	}
	
	public void setAdditionalFields(PeopleFinderAdditionalField pfAdditionalField) {
		this.additionalFields = pfAdditionalField;
	}
	
	public PeopleFinderAdditionalField getAdditionalFields () {
		return additionalFields;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	@Override
	public String toString() {
		StringBuilder params = new StringBuilder();
		params.append("query").append("=").append(query);
		if ( pageSize!=null ){
			params.append("&").append("pageSize").append("=").append(pageSize);
		}
		if ( page!=null ){
			params.append("&").append("page").append("=").append(page);
		}
		if ( startIndex!=null && count!=null){
			params.append("&").append("startIndex").append("=").append(startIndex);
			params.append("&").append("count").append("=").append(count);
		}
		if(boostFriends!=null){
			params.append("&").append("boostFriends").append("=").append(boostFriends);
		}
		if(boostFriendsOfFriends!=null){
			params.append("&").append("boostFriendsOfFriends").append("=").append(boostFriendsOfFriends);
		}
		if(disablePhonetics!=null){
			params.append("&").append("disablePhonetics").append("=").append(disablePhonetics);
		}
		if(highlight!=null){
			params.append("&").append("highlight").append("=").append(highlight);
		}
		if(searchOnlyNameAndEmail!=null){
			params.append("&").append("searchOnlyNameAndEmail").append("=").append(searchOnlyNameAndEmail);	
		}
		if(mustMatchNameOrEmail!=null){
			params.append("&").append("mustMatchNameOrEmail").append("=").append(mustMatchNameOrEmail);
		}
		if(userType!=null){
			params.append("&").append("userType").append("=").append(userType);	
		}
		if(additionalFields!=null){
			params.append("&").append("additionalFields").append("=").append(additionalFields.toString());	
		}
		
		return params.toString();
//		return ToStringBuilder.reflectionToString(this);
		
	}
}