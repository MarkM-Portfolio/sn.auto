package com.ibm.conn.auto.appobjects.member;

import com.ibm.conn.auto.appobjects.Role;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;

public class ActivityCommunityMember extends ActivityMember {

	private BaseCommunity community;
	
	public ActivityCommunityMember(Role role, BaseCommunity communityMember, MemberType type) {
		super(role, null, type);		
		community = communityMember;
	}
	
	public BaseCommunity getCommunity(){
		return community;
	}
	
}
