package com.ibm.conn.auto.appobjects.member;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.Role;

public class ActivityMember extends Member {
	
	private MemberType memberType;

	public ActivityMember(Role role, User user, MemberType type) {
		super(role, user);
		this.setMemberType(type);
	}
	
	public MemberType getMemberType() {
		return memberType;
	}

	public void setMemberType(MemberType type) {
		this.memberType = type;
	}

	public enum MemberType {
		PERSON("Person"),
		COMMUNITY("Community"),
		GROUP("Group");
		
		private String type;
		
		private MemberType(String type) {
			this.type = type;
		}
		
		@Override
	    public String toString(){
			return type;
	    }
	}

}
