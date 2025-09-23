package com.ibm.conn.auto.appobjects.base;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.webui.ActivitiesUI;

public class BaseActivityTemplate implements BaseStateObject {

	private String name;
	private String tags;
	private String description;
	private List<ActivityMember> members = new ArrayList<ActivityMember>();
	
	public static class Builder {
		private String name = null;
		private String tags = null;
		private String description = null;
		private List<ActivityMember> members = new ArrayList<ActivityMember>();
		
		public Builder (String name) {
			this.name = name;
		}
		
		public Builder tags (String tags) {
			this.tags = tags;
			return this;
		}

		public Builder description (String description) {
			this.description = description;
			return this;
		}
		
		public Builder addMembers(List<ActivityMember> members) {
			this.members.addAll(members);
			return this;
		}
		
		public Builder addMember(ActivityMember member) {
			this.members.add(member);
			return this;
		}
		
		public BaseActivityTemplate build() {
			return new BaseActivityTemplate(this);
		}
	}
	
	private BaseActivityTemplate(Builder b) {
		this.setName(b.name);
		this.setTags(b.tags);
		this.setDescription(b.description);
		this.setMembers(b.members);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<ActivityMember> getMembers() {
		return members;
	}
	public void setMembers(List<ActivityMember> members) {
		this.members = members;
	}
	
	public void create(ActivitiesUI ui){
		ui.createTemplate(this);
	}
}
