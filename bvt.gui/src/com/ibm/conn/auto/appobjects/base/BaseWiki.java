package com.ibm.conn.auto.appobjects.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.testng.Assert;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiComment;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

public class BaseWiki implements BaseStateObject{
	
	public enum wikiField{

		NAME,
		TAG,
		DESCRIPTION,
		READACCESS,
		EDITACCESS;	
	}
	
	public enum ReadAccess {
		All(WikisUIConstants.AllUsers_RadioButton),
		WikiOnly(WikisUIConstants.WikiMembersOnly_RadioButton);
		
	    public String readAccessType;
	    private ReadAccess(String level){
	            this.readAccessType = level;
	    }
	    
	    @Override
	    public String toString(){
	            return readAccessType;
	    }
	}
		
	public enum EditAccess {
		AllLoggedIn(WikisUIConstants.AllLoggedInUsers_RadioButton),
		EditorsAndOwners(WikisUIConstants.WikiEditorsAndOwnersOnly_RadioButton);
		
	    public String editAccessType;
	    private EditAccess(String level){
	            this.editAccessType = level;
	    }
	    
	    @Override
	    public String toString(){
	            return editAccessType;
	    }
	}

	private String name;
	private String tags;
	private ReadAccess readAccess;
	private EditAccess editAccess;
	private List<Member> members = new ArrayList<Member>();
	private String description;
	Set<wikiField> edit_track = new HashSet<wikiField>();
	
	public static class Builder {
		private String name;
		private String tags = "";
		private ReadAccess readAccess = ReadAccess.All;
		private EditAccess editAccess = EditAccess.AllLoggedIn;
		private List<Member> members = new ArrayList<Member>();
		private String description;
		
		public Builder(String name){
			this.name = name;
		}

		public Builder tags(String tags){
			this.tags = tags;
			return this;
		}
		
		public Builder readAccess(ReadAccess readAccess){
			this.readAccess = readAccess;
			return this;
		}
		
		public Builder editAccess(EditAccess editAccess){
			this.editAccess = editAccess;
			return this;
		}

		public Builder addMember(Member member) {
			this.members.add(member);
			return this;
		}

		public Builder addMembers(List<Member> members) {
			this.members.addAll(members);
			return this;
		}
		
		
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public BaseWiki build() {
			return new BaseWiki(this);
		}
		

	}
	
	private BaseWiki(Builder wiki) {
		this.setName(wiki.name);
		this.setTags(wiki.tags);
		this.setReadAccess(wiki.readAccess);
		this.setEditAccess(wiki.editAccess);
		this.setMembers(wiki.members);
		this.setDescription(wiki.description);		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		edit_track.add(wikiField.NAME);
		this.name = name;
	}
	
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		edit_track.add(wikiField.TAG);
		this.tags = tags;
	}

	public ReadAccess getReadAccess(){
		return readAccess;
	}  
	
	public void setReadAccess(ReadAccess readAccess) {
		edit_track.add(wikiField.READACCESS);
		this.readAccess = readAccess;
	}

	public EditAccess getEditAccess(){
		return editAccess;
	}  
	
	public void setEditAccess(EditAccess editAccess) {
		edit_track.add(wikiField.EDITACCESS);
		this.editAccess = editAccess;
	}
	
	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}
		
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		edit_track.add(wikiField.DESCRIPTION);
		this.description = description;
	}

	public void create(WikisUI ui) {
		ui.create(this);
		
		//after creating wiki page reset edits
		this.edit_track.clear();
	}
	
	public Wiki createAPI(APIWikisHandler apiOwner) {
		Wiki wiki = apiOwner.createWiki(this);
		Assert.assertTrue(wiki != null, "Failed to add Wiki using API.");
		
		//after creating wiki page reset edits
		this.edit_track.clear();		
		return wiki;
	}

	public void createFollowAPI(APIWikisHandler apiFollower, Wiki wiki){
		apiFollower.createFollow(wiki);
	}

	public WikiPage createWikiPageAPI(APIWikisHandler apiOwner, Wiki wiki, BaseWikiPage baseWikiPage){
			WikiPage wikiPage = apiOwner.createWikiPage(baseWikiPage, wiki);
		return wikiPage;
	}
	
	public void likeWikiPageAPI(APIWikisHandler apiOwner, Wiki wiki, WikiPage wikiPage ){
		apiOwner.likeWikiPage(wikiPage);
	}
	
	public void delete(WikisUI ui, User testUser){
		ui.delete(this, testUser);
	}
	
	public void edit(WikisUI ui, BaseWiki wiki){
		ui.edit(wiki);
		
		//after creating wiki page reset edits
		this.edit_track.clear();	
	}
	
	public void changeAccess(WikisUI ui, BaseWiki wiki){
		ui.changeAccess(wiki);
	}
	
	public Set<wikiField> getEdits(){
		return edit_track;
	}
	
	public WikiComment addCommentAPI(APIWikisHandler apiOwner, WikiPage wikiPage, String wikiCommentContent) {
		return apiOwner.addCommentToWikiPage(wikiPage, wikiCommentContent);
	}
	
	public void editCommentAPI(APIWikisHandler apiOwner,  WikiComment wikiComment, String updatedCommentContent) {
		apiOwner.editCommentOnWikiPage(wikiComment, updatedCommentContent);	
	}

	public void editPageAPI(APIWikisHandler apiOwner, WikiPage wikiPage) {
		apiOwner.editWikiPage(wikiPage);
	}

	public void deletePageAPI(APIWikisHandler apiOwner, Wiki wiki,	WikiPage wikiPage) {
		apiOwner.deleteWikiPage(wiki, wikiPage);
	}

	public void deleteWikiAPI(APIWikisHandler apiOwner, Wiki wiki) {
		apiOwner.deleteWiki(wiki);
	}

	public void deleteCommunityWikiAPI(APIWikisHandler apiOwner, Community community) {
		apiOwner.deleteCommunityWiki(community);
	}
	
	public WikiComment createMentionsCommentAPI(APIWikisHandler apiOwner, WikiPage wikiPage, Mentions mentions){
		return apiOwner.addMentionCommentToWikiPage(wikiPage, mentions);
	}
}