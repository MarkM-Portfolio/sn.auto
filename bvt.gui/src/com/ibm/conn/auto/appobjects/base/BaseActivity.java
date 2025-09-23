package com.ibm.conn.auto.appobjects.base;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.testng.Assert;

import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.conn.auto.appobjects.member.ActivityCommunityMember;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.appobjects.role.CommunityRole;


public class BaseActivity implements BaseStateObject {
	
	public enum activityFields {
		NAME,
		TAGS,
		GOAL;	
	}
	
	private String name;
	private String tags;
	private String template;
	private String goal;
	private List<ActivityMember> members = new ArrayList<ActivityMember>();
	private Calendar dueDate;
	private boolean dueDateRandom;
	private boolean membersTemplate;
	private boolean useCalPick;
	private boolean complete;
	private BaseCommunity community;
	private boolean isPublic;
	private boolean shareExternal;
	private List<ActivityMember> externalMembers = new ArrayList<ActivityMember>();
	private List<ActivityCommunityMember> communityMembers = new ArrayList<ActivityCommunityMember>();
	private ActivityRole implicitRole; 	
	
	public static class Builder {
		private String name;
		private String tags;
		private String template;
		private String goal;
		private List<ActivityMember> members = new ArrayList<ActivityMember>();
		private Calendar dueDate = Calendar.getInstance();
		private boolean dueDateRandom = false;
		private boolean membersTemplate = false;
		private boolean useCalPick = false;
		private boolean complete = false;
		private BaseCommunity community = null;
		private boolean isPublic = true;
		private boolean shareExternal = true;
		private List<ActivityMember> externalMembers = new ArrayList<ActivityMember>();
		private List<ActivityCommunityMember> communityMembers = new ArrayList<ActivityCommunityMember>();
		private ActivityRole implicitRole = null; 
		
		public Builder(String name){
			this.name = name;
		}
		
		public Builder tags(String tags) {
			this.tags = tags;
			return this;
		}
		
		public Builder addMember(ActivityMember member) {
			this.members.add(member);
			return this;
		}
		
		public Builder addMembers(List<ActivityMember> members) {
			this.members.addAll(members);
			return this;
		}
		
		public Builder addExternalMember(ActivityMember member) {
			this.externalMembers.add(member);
			return this;
		}
		
		public Builder addExternalMembers(List<ActivityMember> members) {
			this.externalMembers.addAll(members);
			return this;
		}		
		
		public Builder addCommunityMember(ActivityCommunityMember member){
			this.communityMembers.add(member);
			return this;
		}
		
		public Builder addCommunityMembers(List<ActivityCommunityMember> members){
			this.communityMembers.addAll(members);
			return this;
		}
		
		public Builder implicitRole(ActivityRole role){
			this.implicitRole = role;
			return this;
		}
		public Builder goal(String goal) {
			this.goal = goal;
			return this;
		}
		
		public Builder dueDate(Calendar dueDate) {
			this.dueDate = dueDate;
			return this;
		}
		
		public Builder template(String template) {
			this.template = template;
			return this;
		}
		
		public Builder useMembersFromTemplate() {
			this.membersTemplate = true;
			return this;
		}
		
		public Builder dueDateRandom() {
			this.dueDateRandom = true;
			return this;
		}
		
		public Builder useCalPick(boolean value) {
			this.useCalPick = value;
			return this;
		}
		
		public Builder community(BaseCommunity community){
			this.community = community;
			return this;
		}
		
		public Builder complete(boolean value) {
			this.complete = value;
			return this;
		}
		public Builder isPublic(boolean value){
			this.isPublic=value;
			return this;
			
		}
		
		public Builder shareExternal(boolean value){
			this.shareExternal=value;
			return this;
			
		}
		
		public BaseActivity build() {
			return new BaseActivity(this);
		}

	}
	
	private BaseActivity(Builder b){
		this.setName(b.name);
		this.setTags(b.tags);
		this.setMembers(b.members);
		this.setGoal(b.goal);
		this.setDueDate(b.dueDate);
		this.setTemplate(b.template);
		this.setMembersTemplate(b.membersTemplate);
		this.setDueDateRandom(b.dueDateRandom);
		this.setUseCalPick(b.useCalPick);
		this.setCommunity(b.community);
		this.setComplete(b.complete);
		this.setIsPublic(b.isPublic);
		this.setShareExternal(b.shareExternal);
		this.setExternalMembers(b.externalMembers);
		this.setCommunityMembers(b.communityMembers);
		this.setImplicitRole(b.implicitRole);
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

	public List<ActivityMember> getMembers() {
		return members;
	}

	public void setMembers(List<ActivityMember> members) {
		this.members = members;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public Calendar getDueDate() {
		return dueDate;
	}

	public void setDueDate(Calendar dueDate) {
		this.dueDate = dueDate;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public boolean isMembersTemplate() {
		return membersTemplate;
	}

	public void setMembersTemplate(boolean membersTemplate) {
		this.membersTemplate = membersTemplate;
	}

	public boolean isDueDateRandom() {
		return dueDateRandom;
	}

	public void setDueDateRandom(boolean dueDateRandom) {
		this.dueDateRandom = dueDateRandom;
	}
	
	public boolean getUseCalPick() {
		return useCalPick;
	}

	public void setUseCalPick(boolean useCalPick) {
		this.useCalPick = useCalPick;
	}
	
	public BaseCommunity getCommunity() {
		return community;
	}

	public void setCommunity(BaseCommunity community) {
		this.community = community;
	}
	
	public boolean getComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	public void setIsPublic(boolean value){
		
		this.isPublic=value;
	}
	public boolean isPublic(){
		
		return isPublic;
		
	}
	
	public void setShareExternal(boolean value){
		
		this.shareExternal=value;
	}
	
	public boolean shareExternal(){
		
		return shareExternal;
		
	}
	
	public void create(ActivitiesUI ui) {
		ui.create(this);
	}
	
	public Activity createAPI(APIActivitiesHandler apiOwner) {
		Activity activity = apiOwner.createActivity(this);
		Assert.assertTrue(activity != null, "Failed to add activity using API.");		
		return activity;
	}
	public Activity createAPI(APIActivitiesHandler apiOwner,BaseCommunity baseCom) {
		Activity activity = apiOwner.createActivity(this, baseCom);
		Assert.assertTrue(activity != null, "Failed to add activity using API.");		
		return activity;
	}
	
	public void editGoal(ActivitiesUI ui, String goal) {
		this.setGoal(goal);
		ui.editGoal(this);
	}
	
	public void delete(ActivitiesUI ui) {
		ui.delete(this);
	}

	public List<ActivityMember> getExternalMembers() {
		return externalMembers;
	}

	public void setExternalMembers(List<ActivityMember> externalMembers) {
		this.externalMembers = externalMembers;
	}
	
	public List<ActivityCommunityMember> getCommunityMembers() {
		return communityMembers;
	}

	public void setCommunityMembers(List<ActivityCommunityMember> comMembers) {
		this.communityMembers = comMembers;
	}	

	public ActivityRole getImplicitRole() {
		return implicitRole;
	}

	public void setImplicitRole(ActivityRole implicitRole) {
		this.implicitRole = implicitRole;
	}

	public void createInCommunity(ActivitiesUI ui, CommunityRole memberType) {
		ui.createInCommunity(this, memberType);
	}
	
	public void createInCommunity(ActivitiesUI ui) {
		ui.createInCommunity(this, CommunityRole.OWNERS);
	}
	
}
