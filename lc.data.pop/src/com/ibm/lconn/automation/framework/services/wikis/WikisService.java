package com.ibm.lconn.automation.framework.services.wikis;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

/**
 * Wikis Service object handles getting/posting data to the Connections Wikis service.
 * 
 * @author James Cunningham - jamcunni@ie.ibm.com
 */

public class WikisService extends LCService {
	
	public WikisService(AbderaClient client, ServiceEntry service) {
		super(client, service);
	}
	
	public WikisService(AbderaClient client, ServiceEntry service, Map<String, String> headers) {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}	
	}

	
	public ExtensibleElement createWiki(Wiki wiki) {
		return postFeed(this.service.getServiceURLString() + URLConstants.WIKIS_SERVICE, wiki.toEntry());
	}
	
	public ExtensibleElement createWikiPage(Entry wiki, WikiPage wikiPage) {
		return postFeed(this.service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + encodeURL(wiki.getTitle()) + URLConstants.WIKIS_FEED, wikiPage.toEntry());
	}
	
	public ExtensibleElement createWikiPageInCommunity(String communityUUID, WikiPage wikiPage) {
		return postFeed(service.getServiceURLString() + URLConstants.WIKIS_COMMUNITY_WIKI + communityUUID+ "/feed" , wikiPage.toEntry());
	}
	
	public ExtensibleElement getWikiPagesInCommunity(String communityUUID) {
		return getFeed(service.getServiceURLString() + URLConstants.WIKIS_COMMUNITY_WIKI + communityUUID+ "/feed");
	}
	
	public ExtensibleElement createWikiDraftInCommunity(String communityUUID, String urlParams, String draftName, String draftContent) {
		String url = this.service.getServiceURLString() 
				+ URLConstants.WIKIS_COMMUNITY_WIKI + communityUUID
				+ URLConstants.WIKIS_FEED
				+ "?draft=true";
		
		if(urlParams != null){
			url += urlParams;
		}			

		Entry draftEntry = Abdera.getNewFactory().newEntry();
		draftEntry.addCategory("tag:ibm.com,2006:td/type", "draft", "draft");
		draftEntry.addSimpleExtension("urn:ibm.com/td", "parentUuid", null, communityUUID);
		draftEntry.setTitle(draftName);
		draftEntry.setContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><div>" + draftContent + "</div>", Type.HTML);		
		
		return postFeed(url, draftEntry);
	}
	
	public ExtensibleElement createWikiDraftForPageInCommunity(String wikiUUID, String pageId, String urlParams, String draftName, String draftContent) {
		String url = this.service.getServiceURLString() 
				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wikiUUID
				+ "/page/" + encodeURL(pageId) 
				+ "/entry"
				+ "?draft=true";
		
		if(urlParams != null){
			url += urlParams;
		}	

		Entry draftEntry = Abdera.getNewFactory().newEntry();
		draftEntry.addCategory("tag:ibm.com,2006:td/type", "draft", "draft");
		draftEntry.setTitle(draftName);
		draftEntry.setContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><div>" + draftContent + "</div>", Type.HTML);	
		
		return putFeed(url, draftEntry);
	}
	
	public ExtensibleElement createChildPageUnderWikiPage(String wikiUUID, String parentUUID, WikiPage childPage) {
		String url = this.service.getServiceURLString() 
				+ URLConstants.PUBLIC_WIKIS_FILTER + "/" + wikiUUID
				+ URLConstants.WIKIS_FEED;

		Entry pageEntry = Abdera.getNewFactory().newEntry();
		pageEntry.addCategory("tag:ibm.com,2006:td/type", "page", "page");
		pageEntry.addSimpleExtension("urn:ibm.com/td", "parentUuid", null, parentUUID);
		pageEntry.setTitle(childPage.getTitle());
		pageEntry.setContent(childPage.getContent());		
		
		return postFeed(url, pageEntry);
	}
	
	public ExtensibleElement getWikiFeed(){
		return getFeed(this.service.getServiceURLString() + URLConstants.WIKIS_SERVICE);
	}
	
	public ExtensibleElement getMyWikisFeed() {
		return getFeed(this.service.getServiceURLString() + URLConstants.WIKIS_MY_SERVICE);
	}
	
	public ExtensibleElement getPublicWikisFeed(){
		return getFeed(service.getServiceURLString() + URLConstants.WIKIS_FILTER + "/" + "public");
	}
	
	public ExtensibleElement getPublicWikisFeedUsingLabelFormat(String wikiLabel, String wikiPageLabel){
		// http://lc30linux4.swg.usma.ibm.com/wikis/form/api/wiki/BVT%2520Level%25202%2520Public%2520Wiki%25201931107171/page/New_Peer_for_Public_Wiki_on_CI_Box/feed
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_FORM_FEED + encodeURL(wikiLabel) + "/page/" + encodeURL(wikiPageLabel) + URLConstants.WIKIS_FEED); //convert labels to uri format
	}
	
	public ExtensibleElement getPublicWikisFeed(String para){
		return getFeed(service.getServiceURLString() + URLConstants.WIKIS_FILTER + "/"  + "public?" + para);
	}
	
	public ExtensibleElement getMostCommentedWikisFeed(){
		return getFeed(service.getServiceURLString() + URLConstants.WIKIS_FILTER + "/"  + "mostcommented");
	}

	public ExtensibleElement getMostRecommendedWikisFeed(){
		return getFeed(service.getServiceURLString() + URLConstants.WIKIS_FILTER + "/"  + "mostrecommended");
	}
	
	public ExtensibleElement getWikiPageCommentsEntry(String wikiId, String docId, String commentId){
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + wikiId + "/page/" + docId + "/comment/" + commentId + "/entry");
	}
	
	public ExtensibleElement getHtmlWikiCommentEntry(String wikiId, String docId, String commentId){
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + wikiId + "/page/" + docId + "/comment/" + commentId + "/entry?contentFormat=html");
	}
	
	public ExtensibleElement getRawWikiCommentEntry(String wikiId, String docId, String commentId){
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + wikiId + "/page/" + docId + "/comment/" + commentId + "/entry?contentFormat=raw");
	}
	
	public ExtensibleElement getPublicWikiWithEditLink(String editLink){
		return getFeed(editLink);
	}
	
	public ExtensibleElement updateWikiWithLabel(String wikiLabel, Entry wikiEntry){
		return putFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + encodeURL(wikiLabel) +  "/entry", wikiEntry);
	}
	
	/**
	 * Updates a page with the new updated page entry
	 * @param wikiLabel		Title of the Wiki
	 * @param pageLabel		Title of the Page in the wiki	
	 * @param updatedPageEntry		Updated entry to use
	 * @return	Response
	 */
	public ExtensibleElement updatePage(String wikiLabel, String pageLabel, Entry updatedPageEntry){
		return putFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX+"/"+ encodeURL(wikiLabel) + "/page/" + encodeURL(pageLabel) + "/entry", updatedPageEntry);
	}
	public ExtensibleElement updateDraft(String wikiId, String draftId, Entry updatedDraftEntry){
		String url = service.getServiceURLString()  
 				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wikiId
				+ "/draft/" + draftId 
				+ "/entry";
		return putFeed(url, updatedDraftEntry);
	}
	
	public ExtensibleElement getServiceConfigs(){
		return getFeed(service.getServiceURLString() + URLConstants.ServiceConfigs);
	}
	
	public boolean deleteWiki(String editLink){
		return deleteFeed(editLink);
	}
	
	public String encodeURL(String url) {
		String newURL = "";
		if(url.contains(" ")){
			newURL = url.replace(" ", "%20");
			return newURL;
		}
		
		return url;
	}
	
	public String encodeString(String string){
		String newString = "";
		if(string.contains(" ")){
			newString = string.replace(" ", "+");
			return newString;
		}
		return string;
	}
	
	public ExtensibleElement getMostVisited(){
		return getFeed(service.getServiceURLString() + URLConstants.WIKIS_FILTER + "/"  + "mostvisited");
	}
	
	public ExtensibleElement getPublicWikiWithName(String label) {
		return getFeed(service.getServiceURLString() + URLConstants.PUBLIC_WIKIS_FILTER + "/"  + encodeURL(label) + "/entry");
	}
	
	public ExtensibleElement getWikiPageWithLabels(String wikiLabel, String pageLabel){
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + encodeURL(wikiLabel) + "/page/" + encodeURL(pageLabel) + "/entry");
	}
	
	public ExtensibleElement getEditedPages(String wikiLbl){
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + encodeURL(wikiLbl) + "/mypages");
	}
	
	public ExtensibleElement getEditedPagesPara(String wikiLbl, String para){
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + encodeURL(wikiLbl) + "/mypages?" + para);
	}
	
	public String getPublicWikiTagsWithLabel(String wikiLabel){
		return getResponseString(service.getServiceURLString() + URLConstants.PUBLIC_WIKIS_FILTER + "/"  + encodeURL(wikiLabel) + "/tags");
	}
	
	public ExtensibleElement getListWikisCommunity(String communityId){
		return getFeed(service.getServiceURLString() + "/basic/api/community/" + communityId + "/wikis/feed");
	}
	
	public ExtensibleElement getWikiOfCommunity(String communityId){
		return getFeed(service.getServiceURLString() + "/basic/api/communitywiki/" + communityId + "/entry");
	}
	
	public String getWikiTagsPara(String wikiLbl, String para){
		return getResponseString(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + encodeURL(wikiLbl) + "/tags?" + para);
	}
	
	public ExtensibleElement getWikiTrashPara(String wikiLbl, String para){
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + encodeURL(wikiLbl) + "/recyclebin/feed?" + para);
	}
	
	public ExtensibleElement putWikiOfCommunity(String communityId, Wiki update){
		return putFeed(service.getServiceURLString() + "/basic/api/communitywiki/" + communityId + "/entry", update.toEntry());
	}
	
	public ExtensibleElement getWikiPagesPara(String wikiLbl, String para){
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + encodeURL(wikiLbl) + "/feed?" + para);
	}
	
	public ExtensibleElement getWikiPageResourcesPara(String wikiId, String docId, String para){
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + wikiId + "/page/" + docId + "/feed?" + para);
	}
	
	public String getCommentContent(String wikiId, String pageId, String commentId) {
		return getResponseString(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + wikiId + "/page/" + pageId + "/comment/" + commentId + "/media");
	}
	
	public boolean deleteWikiPage(String wikiId, String pageId){
		return deleteFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + wikiId + "/page/" + pageId + "/entry");
	}
	
	public String getAllWikiTagsPara(String para){
		return getResponseString(service.getServiceURLString() + URLConstants.WIKIS_TAGS +"?scope=library&includeTags=true&" + para);
	}
	
	public ExtensibleElement getWikiMembers(String wikiLabel, String params){
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + encodeURL(wikiLabel) + "/members" + params);
	}
	
	public ExtensibleElement getWikiMembersWithRole(String wikiLabel, String roleType, String params){
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + encodeURL(wikiLabel) + "/roles/" + roleType + "/members" + params);
	}
	
	public ExtensibleElement createWikiComment(String wikiId, String pageId, Entry commentPost){
		return postFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + wikiId + "/page/" + pageId + "/feed", commentPost);
	}
	
	public ExtensibleElement updateWikiComment(String wikiId, String pageId, String commentId, Entry commentPost){
		return putFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + wikiId + "/page/" + pageId + "/comment/" + commentId + "/entry", commentPost);
	}
	
	public boolean deleteWikiComment(String wikiId, String pageId, String commentId){
		return deleteFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + wikiId + "/page/" + pageId + "/comment/" + commentId + "/entry");
	}
	
/*	public ExtensibleElement createWikiDoc(String wikiId, String pageId, Entry docPost){
		return postFeed(service.getServiceURLString() + "/form/api/wiki/"+ wikiId + "/page/" + pageId + "/feed", docPost);
	}*/
	
/*	public ExtensibleElement createRecomendation(String wikiId, String pageId, String userId) throws Exception{
		///page/e8a9ef8c-1ca1-4e12-9ec2-429c129b31bc/recommendation/2bcb7462-785511de-8074e703-179b6183/entry
		//return doPost(service.getServiceURLString() + "/form/api/wiki/" + wikiId + "/page/" + pageId + "/recommendation/" + userId + "/entry", null);
		return postFeed(wikiId, null);
	}*/
	
	public ExtensibleElement genericPost(String URL, Entry entry){
		return postFeed(service.getServiceURLString() + URL, entry);
	}
	
	public ExtensibleElement getListOfPublicWikis(int n){
		return getFeed(service.getServiceURLString() + URLConstants.WIKIS_FILTER + "/public?ps="+n);
	}
	
	public ExtensibleElement getSpecificWikiMember(String wikiLabel, String memberUUID){
		return getFeed(service.getServiceURLString() + URLConstants.PUBLIC_WIKIS_FILTER + "/"+  encodeURL(wikiLabel) + "/members/" + memberUUID);
	}
	
	public ExtensibleElement getListOfWikiRoles(String wikiLabel){
		return getFeed(service.getServiceURLString() + URLConstants.PUBLIC_WIKIS_FILTER + "/" + encodeURL(wikiLabel) + "/roles");
	}
	
	public String getWikiNavigationFeed(String wikiLabel){
		return getResponseString(service.getServiceURLString() + URLConstants.PUBLIC_WIKIS_FILTER + "/" + encodeURL(wikiLabel) + "/navigation/feed");
	}
	
	public String getWikiNavigationFeed(String wikiLabel, String mediaLabel){
		return getResponseString(service.getServiceURLString() + URLConstants.PUBLIC_WIKIS_FILTER + "/" + encodeURL(wikiLabel) + "/navigation/" +encodeURL(mediaLabel) + "/feed");
	}
	
	public ExtensibleElement restorePage(String wikiLbl, String pageId, Entry pageEntry) throws Exception{
		return putFeed(service.getServiceURLString() + "/form/api/wiki/" + wikiLbl + "/recyclebin/feed?undelete=true&itemId=" + pageId + "&nonce=" + getNonce() , pageEntry);
	}
	
	public ExtensibleElement deleteTrashPage(String wikiLbl, String pageId, Entry pageEntry) throws Exception{
		return putFeed(service.getServiceURLString() + "/form/api/wiki/" + wikiLbl + "/recyclebin/feed?delete=true&itemId=" + pageId + "&nonce=" + getNonce() , pageEntry);
	}
	
	public int deleteItem(String url) throws Exception{
		return deleteWithResponseStatus(url);
	}
	
	public String getNonce(){
		return getResponseString(URLConstants.SERVER_URL + URLConstants.FILES_BASE + "/basic/api/nonce");
	}
	
	public void deleteTests(){
		Feed publicWikis = (Feed) getPublicWikisFeed("ps=100");
		for(Entry e : publicWikis.getEntries()){
			if(e.getTitle().equals("wiki-liki")||e.getTitle().equals("Viki") || e.getTitle().equals("CelticsWiki") || e.getTitle().equals("Rapture") || e.getTitle().equals("James's wicked wiki")){
				deleteWiki(e.getEditLinkResolvedHref().toString());
			}
		}
	}
	
	public void deleteWikis(){
		Feed publicWikis = (Feed) getPublicWikisFeed("ps=100");
		for(Entry e : publicWikis.getEntries()){
			deleteWiki(e.getEditLinkResolvedHref().toString());
		}
	}

	public ExtensibleElement getPagesModifiedByUser(String userId) {
		return getFeed(service.getServiceURLString() + URLConstants.WIKIS_PAGES_PERSON + userId + "/feed");
	}

	public boolean deleteMemberFromRole(String wikiLabel, String roleName, String memberUserId) {
		return deleteFeed(service.getServiceURLString()+URLConstants.WIKI_PAGE_URL_PREFIX+"/"+encodeURL(wikiLabel)+"/roles/"+roleName+"/members/"+memberUserId);
	}
	
	public ExtensibleElement getNavigationFeed(String wikiLabel){
		return getFeed(service.getServiceURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wikiLabel + "/navigation/feed?format=xml");
	}

	public String getURLString() {
		return (service.getServiceURLString());
	}
	
	public ExtensibleElement genericGet(String uri){
		return super.getFeed(uri);	
	}
	
	public ExtensibleElement genericPut(String uri, Entry entry){
		return super.putFeed(uri, entry);	
	}
	
	public ClientResponse patchPageLock(String wikiId, String pageId, String lockType) throws Exception{
		String url = service.getServiceURLString()  
 				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wikiId
				+ "/page/" + pageId 
				+ "/entry";			
		
		RequestOptions lockOptions = client.getDefaultRequestOptions();
		lockOptions.setContentType("application/atom+xml;charset=UTF-8");

		/*
		  <entry xmlns="http://www.w3.org/2005/Atom">
    		<td:op type="replace">
        		<td:lock xmlns:td="urn:ibm.com/td" type="hard"></td:lock>
    		</td:op>
		  </entry>
		 */
		QName TD_LOCK = new QName("urn:ibm.com/td", "lock", "td");
		ExtensibleElement lockElement = Abdera.getNewFactory().newElement(TD_LOCK);
	    lockElement.setAttributeValue("type", lockType);	    

		QName TD_OP = new QName("urn:ibm.com/td", "op", "td");
		ExtensibleElement opElement = Abdera.getNewFactory().newElement(TD_OP);
		opElement.setAttributeValue("type", "replace");
		opElement.addExtension(lockElement);
	    
	    Entry entry = Abdera.getNewFactory().newEntry();
	    entry.addExtension(opElement);
		
		return patchResponse(url, entry, lockOptions);
	}
	
}