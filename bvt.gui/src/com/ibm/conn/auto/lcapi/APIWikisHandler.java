package com.ibm.conn.auto.lcapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Text;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.lcapi.common.APIHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiComment;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiMember;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

public class APIWikisHandler extends APIHandler<WikisService> {

	private static final Logger log = LoggerFactory.getLogger(APIWikisHandler.class);
	private String userName;
	private TestConfigCustom cfg;
	
	public APIWikisHandler(String serverURL, String username, String password) {

		super("wikis", serverURL, username, password);
		userName = username;
	}
	
	@Override
	protected WikisService getService(AbderaClient abderaClient, ServiceEntry generalService) {
		return new WikisService(abderaClient, generalService);
	}

	/**
	 * Create a standalone wiki
	 * 
	 * @param wikiObj - The BaseWiki instance from which the wiki will be created
	 * @return - The successfully created Wiki instance if the operation is successful, null otherwise
	 */
	public Wiki createWiki(BaseWiki baseWiki) {
		
		log.info("INFO: Creating a new standalone wiki instance with title: " + baseWiki.getName());
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		
		if (baseWiki.getEditAccess() == EditAccess.AllLoggedIn  && baseWiki.getReadAccess() == ReadAccess.All){
			
			log.info("INFO: Public standalone wiki to be created - all users will be added as readers and editors");
			WikiMember virtualReader = new WikiMember("anonymous-user", WikiRole.READER, WikiMemberType.VIRTUAL);
			WikiMember virtualEditor = new WikiMember("all-authenticated-users", WikiRole.EDITOR, WikiMemberType.VIRTUAL);	
			wikiMembers.add(virtualReader);
			wikiMembers.add(virtualEditor);
		} else {	
			log.info("INFO: Private standalone wiki to be created - only the specified users will be added as members");
			wikiMembers = addMemberToWiki(baseWiki);
		}
		log.info("INFO: The list of wiki members has been created: " + wikiMembers.toString());
		
		// Create the wiki
		Wiki newWiki = new Wiki(baseWiki.getName(), baseWiki.getDescription(), baseWiki.getTags(), wikiMembers);
		Entry createdWikiEntry = (Entry) service.createWiki(newWiki);
		
		// Validate Bookmark created successfully with a POST check
		log.info("Wiki Headers: " + service.getDetail());
		log.info("Checking return code of createWiki call, it should be 201");
		int responseCode = service.getRespStatus();
		if (responseCode != 201){
			log.info("Wiki not created successfully through API, User name: " + userName);
			Assert.fail("User: " + userName + " received response: " 
					+ responseCode + "; expected: 201; Wiki was not created");
		}
		log.info("Wiki successfully created through API");
		log.info("Retrieve that Wiki for full info");
		
		if(APIUtils.resultSuccess(createdWikiEntry, "Wikis")) {
			log.info("INFO: The wiki has been successfully created");
			newWiki.setId(createdWikiEntry.getId());
			newWiki.setLinks(getWikiLinks(createdWikiEntry, true));
			
			return newWiki;
		} else {
			log.info("ERROR: The wiki could not be created");
			log.info(createdWikiEntry.toString());
			return null;
		}
	}
	
	
	public ArrayList<WikiMember> addMemberToWiki(BaseWiki wikiObj){

		ArrayList<WikiMember> members = new ArrayList<WikiMember>();
		List<Member> guiMembers  = wikiObj.getMembers();
		WikiRole userRole;
		
		WikiMember APImember = null;
		
		for (Member guiMember : guiMembers) {
			
			log.info("API: Convert user: " + guiMember.getUser().getDisplayName());
			//convert member role to API
			log.info("API: Convert member role to API");
			if(guiMember.getRole().toString().contentEquals("Editor")){
				log.info("API: Role is Editor");
				userRole = WikiRole.EDITOR;
			} else if(guiMember.getRole().toString().contentEquals("Reader")){
				log.info("API: Role is Reader");
				userRole = WikiRole.READER;
			} else{
				log.info("API: Role is Manager");
				userRole = WikiRole.MANAGER;
			}

			
			APImember = new WikiMember(guiMember.getUUID(), userRole, WikiMemberType.USER);
			log.info("API: Add member to list of Wiki members");
			members.add(APImember);
		}

		return members;
	}
	
	public String getUserUUID(String serverURL, User testUser){
		String returnBlog[] = this.getService().getResponseString(serverURL + "/profiles/atom/profile.do?email=" + testUser.getEmail()).split("snx:userid");
		return returnBlog[1].replace("<", "").replace("/", "").replace(">", "");
	}
	
	/**
	 * This method enables the user to follow a Wiki
	 * 
	 * @param wiki - The wiki which will be followed
	 * @return - True if the follow operation is successful, False otherwise
	 */
	public boolean createFollow(Wiki wiki){
		
		log.info("INFO: Attempting to follow the wiki with title: " + wiki.getTitle());
		return executeFollowWikiRequest(wiki, wiki.getTitle().trim());
	}
	
	/**
	 * Follows a community wiki (NOT a wiki page)
	 * 
	 * @param community - The community in which the wiki to be followed is part of
	 * @return - True if the follow operation is successful, False otherwise
	 */
	public boolean createFollowCommunityWiki(Wiki communityWiki) {
		
		log.info("INFO: Attempting to follow the community wiki in the community with title: " + communityWiki.getTitle());
		return executeFollowWikiRequest(communityWiki, communityWiki.getId().toString());
	}
	
	/**
	 * Creates an entry and performs the follow request for any wiki (either standalone or community)
	 * Private access for now since there is no requirement to use this method externally
	 * 
	 * @param wiki - The wiki to be followed
	 * @param labelText - The identifier to be used in the follow entry (Wiki title for standalone, Wiki external ID for community)
	 * @return - True if the follow wiki operation is successful, False otherwise
	 */
	private boolean executeFollowWikiRequest(Wiki wiki, String labelText) {
		
		log.info("INFO: Executing the follow wiki request for wiki with title: " + wiki.getTitle());
		
		// Create the entry required to follow the wiki
		Entry followEntry = Abdera.getNewFactory().newEntry();
		Element labelElement = (Element) followEntry.addExtension("urn:ibm.com/td", "label", null);
		labelElement.setText(labelText);
		Element followingElement = (Element) followEntry.addExtension("urn:ibm.com/td", "following", null);
		followingElement.setText("on");
		log.info("INFO: The entry to follow the wiki has been created: " + followEntry.toString());
		
		// Create the URL at which to send the entry via a PUT request
		String putRequestURL = wiki.getSelfLink();
		log.info("INFO: The URL at which the PUT request will be sent has been created: " + putRequestURL);
		
		Entry followResult = (Entry) service.genericPut(putRequestURL, followEntry);
		
		if(followResult.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The wiki named " + wiki.getTitle() + " was followed successfully");
			return true;
		} else {
			log.info("ERROR: There was a problem with following the wiki named " + wiki.getTitle());
			log.info(followResult.toString());
			return false;
		}
	}
	
	/**
	 * Creates a wiki page to be added to standalone wiki
	 * 
	 * @param baseWikiPage - The wiki page to be added to the standalone wiki
	 * @param wiki - The wiki in which the wiki page is to be added
	 * @return - A WikiPage instance of the created wiki page if the operation is successful, null otherwise
	 */
	public WikiPage createWikiPage(BaseWikiPage baseWikiPage, Wiki wiki){
		
		log.info("INFO: Creating a new wiki page for the wiki with title: " + wiki.getTitle());
		log.info("PLEASE NOTE: This method only supports adding one tag to the wiki page at present");
		
		// Create the summary text which appears in the news feed in the UI
		String summaryAsText = baseWikiPage.getDescription().trim();
		log.info("INFO: The wiki page summary (news feed content) has been set: " + summaryAsText);
		
		// Create the content which appears in the Wikis UI
		String contentAsHtml = "<p dir=\"ltr\">" + baseWikiPage.getDescription().trim() + "</p>";
		log.info("INFO: The wiki page content has been set: " + contentAsHtml);
		
		// Create the URL to which the create wiki page request will be sent
		String createWikiPageURL = wiki.getRepliesLink().replaceAll(service.getServiceURLString(), "").trim();// + "?tag=" + baseWikiPage.getTags();
		log.info("INFO: The URL for creating the wiki page has been created: " + service.getServiceURLString() + createWikiPageURL);
		
		// Retrieve the tag to be added to this wiki page at creation time
		String tagToAdd;
		try {
			tagToAdd = baseWikiPage.getTags().substring(0, baseWikiPage.getTags().indexOf(' '));
		} catch(StringIndexOutOfBoundsException e) {
			tagToAdd = baseWikiPage.getTags();
		}
		log.info("INFO: The tag to be added to the wiki page has been set: " + tagToAdd);
		
		// Create the entry to represent the wiki page
		Entry wikiPageEntry = createNewWikiPageEntry(baseWikiPage.getName(), summaryAsText, contentAsHtml);
		wikiPageEntry.addCategory(null, tagToAdd, "");
		log.info("INFO: The wiki page entry with tags added has been created: " + wikiPageEntry.toString());
		
		Entry createdWikiPage = (Entry) service.genericPost(createWikiPageURL, wikiPageEntry);
		
		if (APIUtils.resultSuccess(createdWikiPage, "Wikis")) {
			log.info("INFO: Wiki page successfully created and added to the wiki with title: " + wiki.getTitle());
			WikiPage newWikiPage = new WikiPage(createdWikiPage);
			newWikiPage.setId(createdWikiPage.getId());
			newWikiPage.setLinks(getWikiLinks(createdWikiPage, false));
			
			return newWikiPage;
			
		} else {
			log.info("ERROR: The wiki page could not be created");
			log.info(createdWikiPage.toString());
			return null;
		}
	}
	
	/**
	 * Retrieves the Wiki instance of the default-created wiki for the specified community
	 * 
	 * @param community - The Community instance of the community whose Wiki is to be retrieved
	 * @return - The Wiki instance of the community wiki if all operations are successful, null otherwise
	 */
	public Wiki getCommunityWiki(Community community) {
		
		log.info("INFO: Now retrieving the wiki for the community with title: " + community.getTitle());
		
		// Retrieve the community wiki
		Entry communityWikiEntry = getCommunityWikiAsEntry(community);
		if(communityWikiEntry == null) {
			log.info("ERROR: Could not retrieve the parent wiki associated with the community with title: " + community.getTitle());
			return null;
		}
		// Create the community wiki based on the entry
		Wiki communityWiki = new Wiki(communityWikiEntry);		
		communityWiki.setLinks(getWikiLinks(communityWikiEntry, true));
				
		// Retrieve the external ID for this community wiki - this ID is necessary for following the community wiki
		String communityWikiId = communityWikiEntry.toString();
		communityWikiId = communityWikiId.substring(communityWikiId.indexOf("<td:externalInstanceId"),
														communityWikiId.indexOf("</td:externalInstanceId>"));
		communityWikiId = communityWikiId.substring(communityWikiId.lastIndexOf('>') + 1);
		log.info("INFO: The community wiki ID has been retrieved: " + communityWikiId);
		
		// Set the ID to the community wiki
		IRI communityWikiIRIId = new IRI(communityWikiId);
		communityWiki.setId(communityWikiIRIId);
		
		if(community.getCommunityTypeElement().getText().equals("private")) {
			// Set the community wiki to include the members list and set the private flag
			ArrayList<ExtensibleElement> wikiMembersList = getCommunityWikiMembersList(community);
			if(wikiMembersList == null) {
				log.info("ERROR: There was a problem with retrieving the members list for this community");
				return null;
			}
			communityWiki.setPrivateFlag(true);
			communityWiki.setMembers(wikiMembersList);
		}
		log.info("INFO: The parent wiki in the community has been successfully created");
		return communityWiki;
	}
	
	/**
	 * Creates an Entry which is used to create a new WikiPage instance
	 * Private access for now since there is no requirement to use this method externally
	 * 
	 * @param wikiPageTitle - The title of the new wiki page
	 * @param wikiPageSummary - The summary of the new wiki page (this appears in the news feed)
	 * @param wikiPageContent - The content of the new wiki page (this appears in the Wikis screen in the UI)
	 * @return - The Entry object representing the new WikiPage to be created
	 */
	private Entry createNewWikiPageEntry(String wikiPageTitle, String wikiPageSummary, String wikiPageContent) {
		
		log.info("INFO: Creating a new entry for a new wiki page with title: " + wikiPageTitle);
		
		// Create the entry which will represent the new wiki page object
		Entry wikiPageEntry = Abdera.getNewFactory().newEntry();
		wikiPageEntry.addCategory(StringConstants.WIKIS_SCHEME_TYPE, "page", "page");
		wikiPageEntry.setTitle(wikiPageTitle);
		wikiPageEntry.getTitleElement().setTextType(null);
		Element label = wikiPageEntry.addExtension("urn:ibm.com/td", "label", null);
		label.setText(wikiPageTitle);
		Element visibility = wikiPageEntry.addExtension("urn:ibm.com/td", "visibility", null);
		visibility.setText("public");
		Element propagation = wikiPageEntry.addExtension("urn:ibm.com/td", "propagation", null);
		propagation.setText("false");
		wikiPageEntry.setSummary(wikiPageSummary);
		wikiPageEntry.getSummaryElement().setTextType(Text.Type.TEXT);
		wikiPageEntry.setContentAsHtml(wikiPageContent);
		log.info("INFO: The new wiki page entry has been created: " + wikiPageEntry);
		
		return wikiPageEntry;
	}
	
	/**
	 * Allows any wiki page to be followed - either standalone or community
	 * 
	 * @param wikiPage - The wiki page to be followed
	 * @return - True if the operation is successful, False otherwise
	 */
	public boolean createFollowWikiPage(WikiPage wikiPage) {
		
		log.info("INFO: Attempting to follow the wiki page with title: " + wikiPage.getTitle());
		
		// Create the URL to PUT the follow wiki page entry to
		String followURL = wikiPage.getSelfLink();
		log.info("INFO: The URL to send the POST request to has been created: " + followURL);
		
		// Create the entry to PUT to the server
		Entry followWikiPageEntry = Abdera.getNewFactory().newEntry();
		ExtensibleElement notifications = followWikiPageEntry.addExtension("urn:ibm.com/td", "notifications", "");
		notifications.addSimpleExtension("urn:ibm.com/td", "media", "", "on");
		notifications.addSimpleExtension("urn:ibm.com/td", "comment", "", "on");
		log.info("INFO: The entry to follow the wiki page has been created: " + followWikiPageEntry.toString());
		
		Entry putResponse = (Entry) service.genericPut(followURL, followWikiPageEntry);
		
		if(putResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: Wiki page followed successfully");
			return true;
		} else {
			log.info("ERROR: Could not follow the wiki page with title: " + wikiPage.getTitle());
			log.info(putResponse.toString());
			return false;
		}
	}
	
	/**
	 * Returns all relevant Links (Self, Alternate, Edit and Replies links) from an Entry based on a Wiki, Wiki Page or Wiki Comment
	 * Private access at present since there is no requirement to use this method externally
	 * 
	 * @param wikiEntry - The Entry instance from which the links are to be retrieved
	 * @boolean isWikiEntry - If it is a Wiki instance to be updated with Links then this parameter should be True, otherwise False
	 * @return - A HashMap<String, Link> instance of all relevant links
	 */
	private HashMap<String, Link> getWikiLinks(Entry wikiEntry, boolean isWikiEntry) {
		
		log.info("INFO: Retrieving the relevant links for the wiki / wiki page from the entry result");
		HashMap<String, Link> wikiLinks = new HashMap<String, Link>();
		
		wikiLinks.put(StringConstants.REL_SELF + ":" + StringConstants.MIME_NULL, wikiEntry.getSelfLink());
		wikiLinks.put(StringConstants.REL_ALTERNATE + ":" + StringConstants.MIME_TEXT_HTML, wikiEntry.getAlternateLink());
		wikiLinks.put(StringConstants.REL_EDIT + ":" + StringConstants.MIME_ATOM_XML, wikiEntry.getEditLink());
		
		if(isWikiEntry) {
			
			// Create the Replies Link since a Wiki does not automatically generate one
			String selfLinkHref = wikiEntry.getSelfLink().getHref().toString();
			
			String wikiId = wikiEntry.getId().toString().substring(wikiEntry.getId().toString().lastIndexOf(':') + 1);
			String repliesLinkHref = selfLinkHref.substring(0, selfLinkHref.indexOf("/wiki/") + 6) + wikiId + "/feed";
			
			Link repliesLink = Abdera.getNewFactory().newLink();
			repliesLink.setHref(repliesLinkHref);
			repliesLink.setRel(StringConstants.REL_REPLIES);
			
			wikiLinks.put(StringConstants.REL_REPLIES + ":" + StringConstants.MIME_ATOM_XML, repliesLink);
		} else {
			
			// Wiki Pages already have a Replies link
			wikiLinks.put(StringConstants.REL_REPLIES + ":" + StringConstants.MIME_ATOM_XML, wikiEntry.getLink(StringConstants.REL_REPLIES));
		}
		
		return wikiLinks;
	}
	
	public String idToString(IRI id){
		return id.toString().split("urn:lsid:ibm.com:td:")[1];
	}
	
	/**
	 * Edits / updates the parent wiki associated with a community
	 * 
	 * @param community - The community whose parent wiki is to be edited / updated
	 * @return - The Wiki that has been edited / updated if the operation is successful, null otherwise
	 */
	public Wiki editCommunityWiki(Community community) {
		
		log.info("INFO: Editing / updating the wiki in the community with title: " + community.getTitle());
		
		// Retrieve the parent wiki associated with the community
		Entry communityWikiEntry = getCommunityWikiAsEntry(community);
		if(communityWikiEntry == null) {
			log.info("ERROR: Could not retrieve the parent wiki for the community");
			return null;
		}
		log.info("INFO: The parent wiki of the community has been retrieved: " + communityWikiEntry.toString());
		
		// Create the URL to send the PUT request to
		String putRequestURL = communityWikiEntry.getEditLink().getHref().toString();
		log.info("INFO: The URL to PUT the update request to has been created: " + putRequestURL);
		
		Entry updatedWikiEntry = (Entry) service.genericPut(putRequestURL, communityWikiEntry);
		
		if(updatedWikiEntry.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The community wiki was successfully edited / updated");
			Wiki communityWiki = new Wiki(updatedWikiEntry);
			communityWiki.setId(updatedWikiEntry.getId());
			communityWiki.setLinks(getWikiLinks(updatedWikiEntry, true));
			
			return communityWiki;
		} else {
			log.info("ERROR: There was a problem with updating the community wiki");
			log.info(updatedWikiEntry.toString());
			return null;
		}
	}
	
	/**
	 * Edits / updates a wiki page in either a standalone wiki or community wiki by
	 * sending an entry based on itself to the edit link of the existing wiki page
	 * 
	 * @param wikiPage - The wiki page to be edited / updated
	 * @return - The updated wiki page instance if the operation is successful, null otherwise
	 */
	public WikiPage editWikiPage(WikiPage wikiPage) {
		
		log.info("INFO: Editing / updating the existing wiki page with title: " + wikiPage.getTitle());
		
		// Create the necessary entry instance to perform the update
		Entry wikiPageEntry = createNewWikiPageEntry(wikiPage.getTitle().trim(), wikiPage.getSummary().trim(), wikiPage.getSummary().trim());
		log.info("INFO: The updated entry for the wiki page has been created: " + wikiPageEntry.toString());
		
		// Create the URL to PUT the update request to
		String putRequestURL = wikiPage.getEditLink();
		log.info("INFO: The URL at which to send the PUT request has been created: " + putRequestURL);
		
		Entry updatedWikiPageEntry = (Entry) service.genericPut(putRequestURL, wikiPageEntry);
		
		// Return an up to date wiki page instance (including ID and Links)
		if(updatedWikiPageEntry.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The wiki page was edited / updated successfully");
			WikiPage updatedWikiPage = new WikiPage(updatedWikiPageEntry);
			updatedWikiPage.setId(updatedWikiPageEntry.getId());
			updatedWikiPage.setLinks(getWikiLinks(updatedWikiPageEntry, false));
			return updatedWikiPage;
		} else {
			log.info("ERROR: There was a problem with editing / updating the wiki page");
			log.info(updatedWikiPageEntry.toString());
			return null;
		}
	}
	

	public void deleteWikiPage(Wiki wiki, WikiPage wikiPage) {
		service.deleteWikiPage(idToString(wiki.getId()), idToString(wikiPage.getId()));
		
	}
	
	/**
	 * Deletes any wiki page comment from either a standalone or community wiki
	 * 
	 * @param wikiComment - The WikiComment instance of the comment to be deleted
	 * @return - True if the deletion operation is successful, False otherwise
	 */
	public boolean deleteWikiPageComment(WikiComment wikiComment) {
		
		log.info("INFO: Deleting the wiki page comment from the wiki page");
		
		// Retrieve the wiki ID from the wiki comment
		String wikiCommentEditLink = wikiComment.getEditLink();
		String wikiId = wikiCommentEditLink.substring(wikiCommentEditLink.indexOf("/wiki/") + 6,
														wikiCommentEditLink.indexOf("/page/"));
		log.info("INFO: The wiki ID has been retrieved: " + wikiId);
		
		// Retrieve the wiki page ID from the wiki comment
		String wikiPageId = wikiCommentEditLink.substring(wikiCommentEditLink.indexOf("/page/") + 6,
															wikiCommentEditLink.indexOf("/comment/"));
		log.info("INFO: The wiki page ID has been retrieved: " + wikiPageId);
		
		// Retrieve the comment ID from the wiki comment
		String wikiCommentId = wikiCommentEditLink.substring(wikiCommentEditLink.indexOf("/comment/") + 9,
																wikiCommentEditLink.indexOf("/entry"));
		log.info("INFO: The comment ID has been retrieved: " + wikiCommentId);
		
		boolean deleted = service.deleteWikiComment(wikiId, wikiPageId, wikiCommentId);
		
		if(deleted) {
			log.info("INFO: The wiki comment was successfully deleted");
			return true;
		} else {
			log.info("ERROR: The wiki comment could not be deleted");
			return false;
		}
	}

	public void deleteWiki(Wiki wiki) {
		service.deleteWiki(service.getURLString() + URLConstants.WIKI_PAGE_URL_PREFIX + "/"  + wiki.getTitle() + "/entry?");		
	
	}

	public boolean deleteCommunityWiki(Community community) {
		boolean deleted = service.deleteWiki(service.getURLString() + URLConstants.WIKIS_COMMUNITY_WIKI + community.getUuid() + "/entry?");
		
		return deleted;
	
	}
	
	/**
	 * Likes / Recommends a community or standalone wiki page
	 * 
	 * @param wikiPage - The Wiki Page instance which is to be liked / recommended
	 * @return - The self link URL of the recommendation if the operation is successful, null otherwise
	 */
	public String likeWikiPage(WikiPage wikiPage) {

		log.info("INFO: Liking the wiki page with title: " + wikiPage.getTitle());
		
		// Create the recommendation entry to be sent to the URL
		Entry recommendationEntry = Abdera.getNewFactory().newEntry();
		recommendationEntry.addCategory(StringConstants.WIKIS_SCHEME_TYPE, "recommendation", "recommendation");
		log.info("INFO: The recommendation entry has been set: " + recommendationEntry.toString());
		
		// Create the URL to send the recommendation entry to
		String recommendationURL = wikiPage.getRepliesLink() + "?category=recommendation";
		log.info("INFO: The URL has been set: " + recommendationURL);
		
		recommendationURL = recommendationURL.replaceAll(service.getServiceURLString(), "");
		
		Entry recommendationResult = (Entry) service.genericPost(recommendationURL, recommendationEntry);
	
		String entryAlternateLink = recommendationResult.getAlternateLink().getHref().toString();
		if(entryAlternateLink != null && entryAlternateLink.equals(wikiPage.getAlternateLink())) {
			log.info("INFO: The wiki page was successfully liked / recommended");
			return recommendationResult.getSelfLink().getHref().toString();
		} else {
			log.info("ERROR: There was an error with liking / recommending the wiki page");
			log.info(recommendationResult.toString());
			return null;
		}
	}
	
	/**
	 * Unlikes a recommended wiki page
	 * 
	 * @param likeWikiPageURL - The URL of the recommendation that was returned by "liking" the Wiki Page
	 * @return - True if the operation is successful, False otherwise
	 */
	public boolean unlikeWikiPage(String likeWikiPageURL) {
		
		log.info("INFO: Unliking the wiki page");
		
		/* NOTE: 	This does not delete any wiki but this method is the closest match to what is required
		 			in order to delete the recommendation (this just sends a simple deletion request to the URL) */
		return service.deleteWiki(likeWikiPageURL);
	}
	
	/**
	 * Posts a comment to a community or standalone wiki page
	 * 
	 * @param wikiPage - The Wiki Page to which the comment is to be posted
	 * @param commentToBePosted - The comment String to be posted to the Wiki Page
	 * @return - The WikiComment instance of the comment that has been posted, null if the operation fails
	 */
	public WikiComment addCommentToWikiPage(WikiPage wikiPage, String commentToBePosted) {
		
		log.info("INFO: Commenting on the wiki page with title: " + wikiPage.getTitle());
		
		// Create the add comment entry to be sent to the URL
		Entry commentEntry = Abdera.getNewFactory().newEntry();
		commentEntry.addCategory(StringConstants.WIKIS_SCHEME_TYPE, "comment", "comment");
		commentEntry.setContent(commentToBePosted);
		log.info("INFO: The comment entry has been set: " + commentEntry.toString());
		
		// Create the URL to send the recommendation entry to
		String addCommentURL = wikiPage.getRepliesLink();
		log.info("INFO: The URL has been set: " + addCommentURL);
		
		addCommentURL = addCommentURL.replaceAll(service.getServiceURLString(), "");
		Entry addCommentResult = (Entry) service.genericPost(addCommentURL, commentEntry);
		
		if(addCommentResult.getSelfLink() != null) {
			
			log.info("INFO: Comment successfully posted to the wiki page");
			
			// Create the WikiComment instance to be returned
			WikiComment wikiCommentPosted = new WikiComment(commentToBePosted);
			wikiCommentPosted.setId(addCommentResult.getId());
			wikiCommentPosted.setLinks(getWikiLinks(addCommentResult, false));
			return wikiCommentPosted;
			
		} else {
			log.info("ERROR: The comment could not be posted to the wiki page");
			log.info(addCommentResult.toString());
			return null;
		}
	}
	
	/**
	 * Edits a comment posted to a community or standalone wiki page
	 * 
	 * @param wikiPageComment - The WikiComment instance of the wiki page comment which is to be edited
	 * @param updatedCommentMessage - The new message to replace the old comment message string
	 * @return - An updated WikiComment instance if the edit operation is successful, null otherwise
	 */
	public WikiComment editCommentOnWikiPage(WikiComment wikiPageComment, String updatedCommentMessage) {
		
		log.info("INFO: Updating the comment posted to the wiki page");
		
		// Create the add comment entry to be sent to the URL
		Entry commentEntry = Abdera.getNewFactory().newEntry();
		commentEntry.addCategory(StringConstants.WIKIS_SCHEME_TYPE, "comment", "comment");
		commentEntry.setContent(updatedCommentMessage);
		log.info("INFO: The comment entry has been set: " + commentEntry.toString());
		
		// Create the URL to send the recommendation entry to
		String editCommentURL = wikiPageComment.getEditLink();
		log.info("INFO: The URL has been set: " + editCommentURL);
		
		Entry editCommentResult = (Entry) service.genericPut(editCommentURL, commentEntry);
		
		if(editCommentResult.getSelfLink() != null) {
			
			log.info("INFO: The comment has been edited / updated successfully");
			wikiPageComment.setContent(updatedCommentMessage);
			return wikiPageComment;
		} else {
			log.info("ERROR: The comment could not be edited / updated");
			log.info(editCommentResult.toString());
			return null;
		}
	}
	
	/**
	 * Creates a new Wiki Page instance with mentions added to the specified user. The mentions in this case
	 * are included in the description / content of the Wiki Page and NOT in a comment
	 *  
	 * @param parentWiki - The parent wiki in which the wiki page will be added / created
	 * @param baseWikiPage - The base wiki page template instance from which the wiki page will be created
	 * @param mentions - The Mentions instance of the user to be mentioned in the wiki page description
	 * @return - The created WikiPage instance if the operation is successful, null otherwise
	 */
	public WikiPage createWikiPageWithMentions(Wiki parentWiki, BaseWikiPage baseWikiPage, Mentions mentions) {
		
		log.info("INFO: The API will now create the wiki page with mentions to user " + mentions.getUserToMention().getDisplayName());
		
		log.info("INFO: Setting all parameters required to create the new wiki page");
		
		// Create the summary for the wiki page - this acts as the description seen in the news story in the UI
		String summaryWithMention = baseWikiPage.getDescription().trim() + ". " + mentions.getBeforeMentionText().trim() + " "
										+ "<span class=\"vcard\"><span class=\"fn\">" + "@" 
										+ mentions.getUserToMention().getDisplayName().trim() + "</span>" 
										+ "<span class=\"x-lconn-userid\">" + mentions.getUserUUID() 
										+ "</span></span> " + mentions.getAfterMentionText().trim();
		log.info("INFO: The wiki page summary (news story) with added mention String has been created: " + summaryWithMention);
		
		// Create the URL to link to the profile of the user being mentioned
		String userProfileURL = service.getServiceURLString().replace("/wikis", "/profiles")
									+ "/html/profileView.do?userid=" + mentions.getUserUUID();
		log.info("INFO: The profile view URL for the user to be mentioned has been created: " + userProfileURL);
		
		// Create the content for the wiki page - this is required to send the mentioned user a notification which then appears in their Mentions view 
		String contentWithMention = "<p dir=\"ltr\">" + baseWikiPage.getDescription().trim() + ". "
										+ mentions.getBeforeMentionText().trim() + " <span class=\"vcard\">"
										+ "<a class=\"fn url\" role=\"button\" href=\"javascript:;\" _bizcardprocessed_=\"true\" href_bc_=\"" + userProfileURL + "\">"
										+ "@" + mentions.getUserToMention().getDisplayName().trim() + "</a>"
										+ "<span style=\"display: none\" class=\"x-lconn-userid\">"
										+ mentions.getUserUUID().trim() + "</span></span>" 
										+ " " + mentions.getAfterMentionText().trim() + "</p>";
		log.info("INFO: The wiki page content with added mention String has been created: " + contentWithMention);
		
		String createWikiPageURL = parentWiki.getRepliesLink().replaceAll(service.getServiceURLString(), "").trim();
		log.info("INFO: The URL for creating the wiki page has been created: " + service.getServiceURLString() + createWikiPageURL);
		
		// Retrieve the entry which will be sent to the URL to create the wiki page
		Entry wikiPageEntry = createNewWikiPageEntry(baseWikiPage.getName().trim(), summaryWithMention, "");
		wikiPageEntry.getContentElement().setContentType(null);
				
		Entry createdWikiPageEntry = (Entry) service.genericPost(createWikiPageURL, wikiPageEntry);
		
		if(createdWikiPageEntry.toString().indexOf("resp:error=\"true\"") > -1) {
			log.info("ERROR: There was a problem with creating the wiki page");
			log.info(createdWikiPageEntry.toString());
			return null;
		}
		log.info("INFO: Wiki page successfully created");
		
		if(verifyIncludeUserMentions(parentWiki, mentions)) {
			log.info("INFO: Updating the wiki page description to include the mention to user " + mentions.getUserToMention().getDisplayName());
			wikiPageEntry.setContentAsHtml(contentWithMention);
			Entry updatedWikiPageEntry = (Entry) service.genericPut(createdWikiPageEntry.getEditLink().getHref().toString(), wikiPageEntry);
			
			// Return an up to date wiki page instance (including ID and Links)
			if(updatedWikiPageEntry.toString().indexOf("resp:error=\"true\"") == -1) {
				log.info("INFO: Wiki page successfully updated with mentions to user " + mentions.getUserToMention().getDisplayName());
				WikiPage wikiPage = new WikiPage(updatedWikiPageEntry);
				wikiPage.setId(createdWikiPageEntry.getId());
				wikiPage.setLinks(getWikiLinks(updatedWikiPageEntry, false));
				return wikiPage;
			}
		}
		log.info("INFO: The wiki page could not be updated with the mentions to user " + mentions.getUserToMention().getDisplayName() + " since they are not a member of the parent wiki");
		
		// Create and return a wiki page based on the created wiki page
		WikiPage wikiPage = new WikiPage(createdWikiPageEntry);
		wikiPage.setId(createdWikiPageEntry.getId());
		wikiPage.setLinks(getWikiLinks(createdWikiPageEntry, false));
		
		return wikiPage;
	}
	
	/**
	 * Verifies if the mention to another user in the wiki page content should be included
	 * Private access for now since there is no requirement for this method to be used externally
	 * 
	 * @param wiki - The parent wiki of the wiki page in which the user is to be mentioned
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @return - True if the mentions to the user should be included, false otherwise
	 */
	private boolean verifyIncludeUserMentions(Wiki wiki, Mentions mentions) {
		
		log.info("INFO: Verifying if the mentions notification should be sent to user with title: " + mentions.getUserToMention().getDisplayName());
		
		boolean includeMentions;
		if(wiki.getPrivateFlag() == true && wiki.getMembers() != null) {
			log.info("INFO: The parent wiki is a private / restricted wiki - checking the members list to verify that " 
						+ mentions.getUserToMention().getDisplayName() + " is a member of the wiki");
			ArrayList<ExtensibleElement> listOfMembers = wiki.getMembers();
			includeMentions = false;
			int index = 0;
			while(index < listOfMembers.size() && includeMentions == false) {
				ExtensibleElement currentMember = listOfMembers.get(index);
				if(currentMember.getAttributeValue("ca:id").equals(mentions.getUserUUID())) {
					log.info("INFO: " + mentions.getUserToMention().getDisplayName() + " is a member of the parent wiki - will attempt to send the mention to the user");
					includeMentions = true;
				}
				index ++;
			}
		} else if(wiki.getPrivateFlag() == true && wiki.getMembers() == null) {
			log.info("INFO: The parent wiki is a private / restricted wiki with no members list attached");
			log.info("INFO: Cannot send mention notification to " + mentions.getUserToMention().getDisplayName());
			includeMentions = false;
		} else if(wiki.getPrivateFlag() == false) {
			log.info("INFO: The parent wiki is a public wiki - will attempt to send the mention to the user");
			includeMentions = true;
		} else {
			log.info("INFO: The parent wiki has no members list attached - will attempt to send the mention to the user");
			includeMentions = true;
		}
		return includeMentions;
	}
	
	/**
	 * Retrieves the list of members for a community wiki and returns them in the correct format
	 * Private access for now since there is no requirement to use this method externally
	 * 
	 * @param community - The community which contains the wiki
	 * @return - The list of wiki members if the operation is successful, null otherwise
	 */
	private ArrayList<ExtensibleElement> getCommunityWikiMembersList(Community community) {
		
		log.info("INFO: Retrieving the members list for the community with title: " + community.getTitle());
		ArrayList<ExtensibleElement> wikiMembers = new ArrayList<ExtensibleElement>();
		
		// Retrieve the members feed for the community
		Feed membersFeed = (Feed) service.genericGet(community.getMembersListHref());
		List<Entry> listOfMembers = membersFeed.getEntries();
		
		int index = 0;
		while(index < listOfMembers.size()) {
			
			// Retrieve the user ID for this member from the entry
			String currentMemberString = listOfMembers.get(index).toString();
			String userId = currentMemberString.substring(currentMemberString.indexOf("snx:userid"),
															currentMemberString.indexOf("</snx:userid>"));
			userId = userId.substring(userId.lastIndexOf('>') + 1);
			log.info("INFO: Community member found with user ID: " + userId);
			
			log.info("INFO: Creating member element for user with ID " + userId);
			ExtensibleElement member = Abdera.getNewFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/composite-applications/v1.0", "member"));
			member.setAttributeValue("xmlns:ca", "http://www.ibm.com/xmlns/prod/composite-applications/v1.0");
			member.setAttributeValue("ca:id", userId);
			member.setAttributeValue("ca:type", "user");
			member.setAttributeValue("ca:role", "editor");
			
			wikiMembers.add(member);
			index ++;
			log.info("INFO: New member element created and added to the members list: " + member.toString());
		}
		
		if(wikiMembers.size() == 0) {
			return null;
		} else {
			return wikiMembers;
		}
	}
	
	/**
	 * Retrieves the entry of the wiki instance associated with any community
	 * Private access for now since there is no requirement to use this method externally
	 * 
	 * @param community - The community in which the wiki instance is to be retrieved
	 * @return - The community wiki entry if the operation is successful, null otherwise
	 */
	private Entry getCommunityWikiAsEntry(Community community) {
		
		log.info("INFO: Retrieving the Wiki entry associated with the community with title: " + community.getTitle());
		
		// Retrieve the wiki entry associated with the community
		Entry communityWikiEntry = (Entry) service.getWikiOfCommunity(community.getUuid());
		
		if(communityWikiEntry.toString().indexOf("resp:error=\"true\"") > -1) {
			log.info("ERROR: There was a problem with retrieving the community wiki");
			log.info(communityWikiEntry.toString());
			return null;
		}
		log.info("INFO: The community wiki entry has been retrieved: " + communityWikiEntry.toString());
		
		return communityWikiEntry;
	}
	
	/**
	 * Posts a comment to any wiki page and includes a mention to the specified user
	 * 
	 * PLEASE NOTE:
	 * If you are attempting to post a wiki comment with a mention to a community member in a 
	 * Restricted Community -> In these cases you should ensure that the user that you are mentioning 
	 * in the comment is both a member of the community and is also following the wiki page using
	 * the createFollowWikiPage() method.
	 * 
	 * @param wikiPage - The wiki page to which the comment is to be posted
	 * @param userToMention - The Mentions instance of the user to be mentioned in the comment
	 * @return - The WikiComment instance of the successfully posted comment if the operation is successful, null otherwise
	 */
	public WikiComment addMentionCommentToWikiPage(WikiPage wikiPage, Mentions userToMention) {
		cfg = TestConfigCustom.getInstance();
		
		String contentWithMention;
		Entry wikiCommentEntry;
		log.info("INFO: Adding a mention to " + userToMention.getUserToMention().getDisplayName()
				+ " in a comment to be added to the wiki page with title: " + wikiPage.getTitle());

		if (cfg.getTestConfig().serverIsMT()) {
			
			// Create the content of the wiki page comment that will include the mention
			contentWithMention = userToMention.getBeforeMentionText() + " "
					+ "<span class=\"vcard\"><span class=\"fn\">@@" + userToMention.getUserToMention().getDisplayName()
					+ "</span><span class=\"x-lconn-userid\">" + userToMention.getUserUUID() + "</span></span> "
					+ userToMention.getAfterMentionText();
			log.info("INFO: The wiki page comment content with mention has been created: " + contentWithMention);
			
		} else {

			// Create the content of the wiki page comment that will include the mention
			 contentWithMention = userToMention.getBeforeMentionText() + " "
					+ "<span class=\"vcard\"><span class=\"fn\">@" + userToMention.getUserToMention().getDisplayName()
					+ "</span><span class=\"x-lconn-userid\">" + userToMention.getUserUUID() + "</span></span> "
					+ userToMention.getAfterMentionText();
			log.info("INFO: The wiki page comment content with mention has been created: " + contentWithMention);
		}
		// Create the entry to represent the wiki comment
		wikiCommentEntry = Abdera.getNewFactory().newEntry();
		wikiCommentEntry.addCategory(StringConstants.WIKIS_SCHEME_TYPE, "comment", "comment");
		wikiCommentEntry.setContentAsHtml(contentWithMention);

		String postRequestURL = wikiPage.getRepliesLink().replaceAll(service.getServiceURLString(), "").trim();
		log.info("INFO: The URL to POST the wiki comment entry to has been created: " + service.getServiceURLString()+ postRequestURL);

		Entry newCommentEntry = (Entry) service.genericPost(postRequestURL, wikiCommentEntry);
		
		if(newCommentEntry.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: Comment with mention successfully added to the wiki page");
			WikiComment wikiComment = new WikiComment(newCommentEntry);
			wikiComment.setId(newCommentEntry.getId());
			wikiComment.setLinks(getWikiLinks(newCommentEntry, false));
			
			return wikiComment;
		} else {
			log.info("ERROR: The comment could not be added to the wiki page");
			log.info(newCommentEntry.toString());
			return null;
		}
	}
}
