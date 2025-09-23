package com.ibm.conn.auto.appobjects.base;

import java.util.HashSet;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import org.testng.Assert;

import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BaseDogear implements BaseStateObject{

	public enum Access {
		PUBLIC(DogearUIConstants.Form_AddBookmark_Radio_Public),
		RESTRICTED(DogearUIConstants.Form_AddBookmark_Radio_Private);
		
	    public String bookmarkType;
	    private Access(String level){
	            this.bookmarkType = level;
	    }
	    
	    @Override
	    public String toString(){
	            return bookmarkType;
	    }
	}

	public enum dogearFields {
		TITLE,
		URL,
		TAGS,
		DESCRIPTION,
		IS_IMPORTANT,
		ACCESS,
		COMMUNITY;
	}
	
	private String title;
	private String url;
	private String tags;
	private String description;
	private boolean isImportant;
	private Access access;
	private BaseCommunity community;
	private String UUID;
	private Set<dogearFields> edit_track = new HashSet<dogearFields>();
	
	public static class Builder {
		private String title;
		private String url;
		private String tags;
		private String description;
		private boolean isImportant = false;
		private Access access = Access.PUBLIC;
		private BaseCommunity community = null;
		
		public Builder(String title, String url){
			this.title = title;
			this.url = url;
		}
		
		public Builder tags(String tags){
			this.tags = tags;
			return this;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Builder isImportant(boolean isImportant) {
			this.isImportant = isImportant;
			return this;
		}
		
		public Builder access(Access access) {
			this.access = access;
			return this;
		}

		public Builder community(BaseCommunity community){
			this.community = community;
			return this;
		}
		
		public BaseDogear build() {
			return new BaseDogear(this);
		}

	}
	
	private BaseDogear(Builder b) {
		this.setTitle(b.title);
		this.setURL(b.url);
		this.setTags(b.tags);
		this.setDescription(b.description);
		this.setIsImportant(b.isImportant);
		this.setAccess(b.access);
		this.setCommunity(b.community);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String name) {
		edit_track.add(dogearFields.TITLE);
		this.title = name;
	}
	
	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		edit_track.add(dogearFields.URL);
		this.url = url;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		edit_track.add(dogearFields.TAGS);
		this.tags = tags;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		edit_track.add(dogearFields.DESCRIPTION);
		this.description = description;
	}
	
	public boolean getIsImportant() {
		return isImportant;
	}

	public void setIsImportant(boolean isImportant) {
		edit_track.add(dogearFields.IS_IMPORTANT);
		this.isImportant = isImportant;
	}
	
	public Access getAccess() {
		return access;
	}

	public void setAccess(Access access) {
		edit_track.add(dogearFields.ACCESS);
		this.access = access;
	}

	public BaseCommunity getCommunity() {
		return community;
	}

	public void setCommunity(BaseCommunity community) {
		edit_track.add(dogearFields.COMMUNITY);
		this.community = community;
	}	
	
	public Set<dogearFields> getEdits(){
		return edit_track;
	}
	
	
	public void create(DogearUI ui) {
		if (this.getCommunity() == null){
			ui.create(this);
		}else{
			ui.create(this, this.getCommunity());
		}
		//after creating bookmark reset edits
		this.edit_track.clear();		
	}
	
	public void edit(DogearUI ui){
		if (this.getCommunity() == null){
			ui.edit(this);
		}else{
			ui.edit(this, this.getCommunity());
		}
		//after creating bookmark reset edits
		this.edit_track.clear();
	}
	
	public void editWithDropDown(DogearUI ui){
		if (this.getCommunity() == null){
			ui.editUsingDropdown(this);
		}else{
			ui.editUsingDropdown(this, this.getCommunity());
		}
		//after creating bookmark reset edits
		this.edit_track.clear();
	}
	
	public void delete(DogearUI ui) {	
		ui.delete(this);
	
	}
	
	public String getUUID(DogearUI ui) {
		ui.getUUID(this);
		return UUID;
	}

	public void setUUID(String UUID) {
		this.UUID = UUID;
	}

	/**
	 * Add an application bookmark via API 
	 * @param APIDogearHandler apiOwner
	 * @return Bookmark API object
	 * @see createAPI(APICommunitiesHandler apiOwner) if you want to add community level bookmark
	 */
	public Bookmark createAPI(APIDogearHandler apiOwner){
		
		Bookmark bookmark = null;
		
		if(this.getCommunity()==null){
			bookmark = apiOwner.createBookmark(this);
			Assert.assertTrue(bookmark != null, "Failed to add bookmark using API.");				
		}
		
		//after creating bookmark reset edits
		this.edit_track.clear();	
		
		return bookmark;
	}
	
	/**
	 * Add a bookmark to a community via API
	 * @param APICommunitiesHandler apiOwner
	 * @return Community API object
	 * @see createAPI(APIDogearHandler apiOwner) if you want to add application level bookmark
	 */
	public Community createAPI(APICommunitiesHandler apiOwner) {
		Community community = null;
		
		if(this.getCommunity()!=null){
			community = apiOwner.createBookmark(this);	
		}
		
		//after creating bookmark reset edits
		this.edit_track.clear();		
		
		return community;
	}
}
