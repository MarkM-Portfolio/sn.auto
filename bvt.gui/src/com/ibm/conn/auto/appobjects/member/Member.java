package com.ibm.conn.auto.appobjects.member;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.Role;

public class Member {
	
	private Role role;
	private User user;
	private String UUID;

	
	public Member(Role role, User user) {
		this.role = role;
		this.user = user;
		this.UUID = "";
	}
	
	public Member(Role role, User user, String UUID){
		this.role = role;
		this.user = user;
		this.UUID = UUID;
	}
	
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUUID(){
		return UUID;
	}
	
	public void setUUID(String UUID) {
		this.UUID = UUID;
	}
	
}
