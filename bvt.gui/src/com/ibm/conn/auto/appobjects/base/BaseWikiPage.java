package com.ibm.conn.auto.appobjects.base;

import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;

import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.appobjects.base.BaseWiki.Builder;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;


public class BaseWikiPage implements BaseStateObject{

	public enum wikiPageField {

		NAME,
		TAG,
		DESCRIPTION,
		PAGE_TYPE;
	}
	
	/** Wiki Page create options. */
	public enum PageType {
		Community,
		Peer,
		Child,
		NavPage,
		Context_Peer,
		Context_Child,
		About_Child;
	}

	private String name;
	private String tags;
	private String description;
	private PageType pageType;
	Set<wikiPageField> edit_track = new HashSet<wikiPageField>();
	private int likeCount = 0;
	private String tinyEditorFunctionalitytoRun;

	public static class Builder {
		private String name;
		private String tags = "";
		private String description;
		private PageType pageType;
		private String tinyEditorFunctionalitytoRun;
		
		public Builder(String name, PageType pageType){
			this.name = name;
			this.pageType = pageType;
		}

		public Builder tags(String tags){
			this.tags = tags;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public BaseWikiPage build() {
			return new BaseWikiPage(this);
		}
		
		public Builder tinyEditorFunctionalitytoRun(String functionality)
		{
			this.tinyEditorFunctionalitytoRun= functionality;
			return this;
		}

	}
	
	private BaseWikiPage(Builder wikiPage) {
		this.setName(wikiPage.name);
		this.setTags(wikiPage.tags);
		this.setDescription(wikiPage.description);
		this.setPageType(wikiPage.pageType);
		this.setLikeCount(0);
		this.setTinyEditorFunctionalitytoRun(wikiPage.tinyEditorFunctionalitytoRun);
		}

	public void setTinyEditorFunctionalitytoRun(String functionality)
	{
		this.tinyEditorFunctionalitytoRun = functionality;
	}
	
	public String getTinyEditorFunctionalitytoRun() {
		return tinyEditorFunctionalitytoRun;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		edit_track.add(wikiPageField.NAME);
		this.name = name;
	}
	
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		edit_track.add(wikiPageField.TAG);
		this.tags = tags;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		edit_track.add(wikiPageField.DESCRIPTION);
		this.description = description;
	}
	
	public PageType getPageType() {
		return pageType;
	}

	public void setPageType(PageType pageType) {
		this.pageType = pageType;
	}

	public int getLikeCount(){
		return likeCount;
	}
	
	public void setLikeCount(int likeCount){
		this.likeCount = likeCount;
	}
	
	
	public void create(WikisUI ui) {
		ui.createPage(this);
		
		//after creating wiki page reset edits
		this.edit_track.clear();
	}
	
	public Wiki createAPI(APIWikisHandler apiOwner, Wiki wiki) {
		WikiPage wikiPage = apiOwner.createWikiPage(this, wiki);
		Assert.assertTrue(wikiPage != null, "Failed to add Wiki using API.");	

		//after creating wiki page reset edits
		this.edit_track.clear();
		return wiki;
	}
	
	public void delete(WikisUI ui) {
		ui.deletePage(this);
	}
	
	public void edit(WikisUI ui) {
		ui.editWikiPage(this);
	}
	
	public void like(WikisUI ui){
		ui.likeUnlikePage("Like");
		this.setLikeCount(this.getLikeCount() + 1);
	}
	
	public void unlike(WikisUI ui){
		ui.likeUnlikePage("Unlike");
		this.setLikeCount(this.getLikeCount() - 1);
	}
	
	public void restore(WikisUI ui){
		ui.restore(this);
	}
	
	public Set<wikiPageField> getEdits(){
		return edit_track;
	}
}
