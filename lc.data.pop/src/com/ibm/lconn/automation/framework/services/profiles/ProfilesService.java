package com.ibm.lconn.automation.framework.services.profiles;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.client.AbderaClient;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.ProfileFormat;
import com.ibm.lconn.automation.framework.services.common.StringConstants.ProfileOutput;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Comment;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Connection;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Message;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Profile;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Status;
import com.ibm.lconn.automation.framework.services.profiles.nodes.TagsEntry;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;

/**
 * Profiles Service object handles getting/posting data to the Connections Profiles service.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ProfilesService extends LCService {

	private HashMap<String, String> profileURLs;
	private HashMap<String, Element> editableFields;
	private String generator = "";
	
	public ProfilesService(AbderaClient client, 
			ServiceEntry service) throws LCServiceException {
		super(client, service);
		updateServiceDocument();
	}
	
	public ProfilesService(AbderaClient client, 
			ServiceEntry service, Map<String, String> headers) throws LCServiceException {
		super(client, service);
		for(String key : headers.keySet()){	
			//if ( key.equalsIgnoreCase("Authorization")){
				//options.setAuthorization(headers.get(key));

			//} else {
				this.options.setHeader(key, headers.get(key));
			//}
		}	
		updateServiceDocument();
	}

	private void updateServiceDocument() throws LCServiceException {
		editableFields = new HashMap<String, Element>();

		ExtensibleElement feed = getFeed(service.getServiceURLString() + URLConstants.PROFILES_SERVICE);
		//ExtensibleElement feed = getFeedWithRedirect(service.getServiceURLString() + URLConstants.PROFILES_SERVICE);
		
		if(feed != null) {
			if(getRespStatus() == 200) {
				setFoundService(true);
				profileURLs = getCollectionUrls((Service) feed);
			
				Element gen = feed.getFirstChild(StringConstants.ATOM_GENERATOR);
				if (gen != null) {
					generator = gen.getText();
				}
			} else {
				setFoundService(false);
				throw new LCServiceException("Error : Can't get ProfilesService Feed, status: " + getRespStatus());
			}
		} else {
			setFoundService(false);
			throw new LCServiceException("Error : Can't get ProfilesService Feed, status: " + getRespStatus());
		}
	}
	
	public String getGenerator() {
		return generator;
	}
	
	public VCardEntry getUserVCard() {
		VCardEntry vCard = null;
		
		ExtensibleElement vCardFeed = getFeed(profileURLs.get(StringConstants.REL_VCARD).toString());
		if(vCardFeed != null) {
			// Find the vCard content in the profile feed entry.
			for(Entry entry : ((Feed) vCardFeed).getEntries()) {
				vCard = new VCardEntry(entry.getContent(), editableFields);
			}
		}
		
		return vCard;
	}
	
	public ExtensibleElement getUserProfile() {
		
		return getFeed(profileURLs.get(StringConstants.REL_VCARD).toString().substring(0,profileURLs.get(StringConstants.REL_VCARD).toString().indexOf("&output=vcard")));
	}
	
	public String getUserId() {
		String retval = null;
		try {
		ExtensibleElement el = getUserProfile();
		Feed profileFeed = (Feed)el;
		Entry profileEntry = profileFeed.getEntries().get(0);
		retval = profileEntry.getContributors().get(0).getSimpleExtension(StringConstants.SNX_USERID);
		} catch (Exception ex) {
			System.out.println("ProfileService: Failed to get userId, ex = " +ex);
		}
		return retval;
	}

	public String getUserKey() {
		String retval = null;
		try {
			retval = getUserVCard().getVCardFields().get("X_PROFILE_KEY");
			
		} catch (Exception ex) {
			System.out.println("ProfileService: Failed to get userKey, ex = " +ex);
		}
		return retval;
	}
	
	public boolean updateProfile(VCardEntry vCardEntry) {
		boolean success = false;
		ExtensibleElement vCardFeed = getFeed(profileURLs.get(StringConstants.REL_VCARD).toString());
		
		if (vCardFeed != null) {
			// Find the vCard content in the profile feed entry.
			for(Entry entry : ((Feed) vCardFeed).getEntries()) {
				ExtensibleElement result = putFeed(entry.getLink(StringConstants.REL_EDIT).getHref().toString(), vCardEntry.toEntry());
				if(result != null) {
					success = true;
				}
			}
		}
		
		return success;
	}
	
	public String getPermaLink(String userId) {
		/*HttpResponse result = null;
		try {
			result =  doHttpGet(service.getServiceURLString()+ "/atom/html/profileView.do?userid=" + userId);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return getResponseString(service.getServiceURLString()+ "/atom/html/profileView.do?userid=" + userId);
	}
	
	public ExtensibleElement getProfileStatus() {
		return getFeed(profileURLs.get("status").toString());
	}
	
	public ExtensibleElement getProfileType(String sType) {
		return getFeed(profileURLs.get("profile-type").toString().replace("default",sType));
	}

	public ExtensibleElement setProfileStatus(Status newStatus) {
		return putFeed(profileURLs.get("status").toString(), newStatus.toEntry());
	}
	
	public boolean clearProfileStatus() {
		return deleteFeed(profileURLs.get("status").toString());
	}
	
	public ExtensibleElement getStatus(String profile){
		return getFeed(service.getServiceURLString() + "/atom/mv/theboard/entries/all.do?key="+ profile);
	}
	
	public String getLegacyStatusTitle(){
		String result = null;
		
		ExtensibleElement ee = getFeed(service.getServiceURLString() + "/atom/forms/mv/theboard/entry/status.do");
		if (ee == null)
			return result;
		
		// Check for invalid response error and return empty array instead of 
		// getting an exception trying to parse
		Element e = ee.getExtension(StringConstants.API_RESPONSE_CODE);
		if (e != null) {
			int statusCode = Integer.parseInt(e.getText());
			if (statusCode > 200)
				return result;
		}
		
		Entry theEntry = (Entry) ee;
		if (theEntry != null)
			result =  theEntry.getTitle();
		
		return result;
		
	}
	
	public String checkLegacyFeed(String id){
		String result = null;
		
		ExtensibleElement ee = getFeed(service.getServiceURLString() + "/atom/mv/theboard/entry/status.do?entryId="+id);
		if (ee == null)
			return result;
		
		return ee.toString();
		
	}
	
	public ExtensibleElement getStatusFromMult(String profile1, String profile2){
		return getFeed(service.getServiceURLString() + "/atom/mv/theboard/entries/all.do?key="+ profile1 + "&key=" + profile2);
	}
	
	
	public ExtensibleElement getBoardMessages() {
		return getFeed(profileURLs.get("theboard").toString());
	}
	
	public ExtensibleElement getLegacyBoardMessages(String userId) {
		String theboard = profileURLs.get("theboard").toString();
		theboard = theboard.substring(0, theboard.indexOf("?key"))+"?userid="+userId+"&comments=all&sortOrder=desc";
		return getFeed(theboard);
	}
	
	public ExtensibleElement addBoardMessage(Message newMessage) {
		return postFeed(profileURLs.get("theboard").toString(), newMessage.toEntry());
	}
	
	//get feed from a passed url
	public ExtensibleElement getUrlFeed(String url){
		return getFeed(url);
	}
	
	//delete a feed based on passed url
	public boolean deleteUrlFeed(String url){
		return deleteFeed(url);
	}
	
	
	// Can be used for message board or status feed replies.
	public ExtensibleElement addBoardMessageReply(Comment newComment, String replyLink) {
		return postFeed(replyLink, newComment.toEntry());
	}
	
	public Categories getProfileTags(String targetEmail, String targetKey) {
		Categories categories = null;
		String searchPath = getTagSearchPath(targetEmail, targetKey);
		
		if(searchPath != null) {
			ExtensibleElement feed = getFeed(searchPath);

			if(feed != null) {
				categories = (Categories) feed;
			}
		}
		return categories;
	}
	
	public ExtensibleElement setProfileTags(TagsEntry tags, String targetEmail, String targetKey, String sourceEmail, String sourceKey) {
		ExtensibleElement feed = null;
		String postPath = getTagPostPath(targetEmail, targetKey, sourceEmail, sourceKey);
		
		if(postPath != null) {
			feed = putFeed(postPath, tags.toCategories());
		}

		return feed;
	}
	
	public ExtensibleElement inviteColleague(Connection newConnection, String userEmail, String userKey) {
		ExtensibleElement feed = null;
		String colleaguePath = getColleaguePath(userEmail, userKey);
		
		if(colleaguePath != null) {
			feed = postFeed(colleaguePath, newConnection.toEntry());
		}
		
		return feed;
	}
	
	public ExtensibleElement checkColleagueStatus(String sourceEmail, String sourceKey, String targetEmail, String targetKey) {
		String path = service.getServiceURLString() + URLConstants.PROFILES_SEARCH_ARE_COLLEAGUES;
	
		if(sourceKey != null && sourceKey.length() != 0) {
			
			if(targetKey != null && targetKey.length() != 0) {
				path += "&sourceKey=" + sourceKey + "&targetKey=" + targetKey;
			} else if(targetEmail != null && targetEmail.length() != 0) {
				path +=  "&sourceKey=" + sourceKey + "&targetEmail=" + targetEmail;
			} else {
				path = null;
			}
			
		} else 	if(sourceEmail != null && sourceEmail.length() != 0) {
			if(targetEmail != null && targetEmail.length() != 0) {
				path += "&sourceEmail=" + sourceEmail + "&targetEmail=" + targetEmail;
			} else if(targetKey != null && targetKey.length() != 0) {
				path += "&sourceEmail=" + sourceEmail + "&targetKey=" + targetKey;
			} else {
				path = null;
			}
			
		}
		
		return getFeed(path);
	}
	
	private String getTagSearchPath(String targetEmail, String targetKey) {
		String searchPath = profileURLs.get("tag-cloud").toString();
		
		if(targetEmail != null && targetEmail.length() != 0) {
			searchPath += "?targetEmail=" + targetEmail;
		} else if(targetKey != null && targetKey.length() != 0) {
			searchPath += "?targetKey=" + targetKey;
		} else {
			searchPath = null;
		}
		
		return searchPath;
	}
	
	private String getTagPostPath(String targetEmail, String targetKey, String sourceEmail, String sourceKey) {
		String searchPath = profileURLs.get("tag-cloud").toString();
		
		if(targetEmail != null && targetEmail.length() != 0) {
			
			if(sourceEmail != null && sourceEmail.length() != 0) {
				searchPath += "?targetEmail=" + targetEmail + "&sourceEmail=" + sourceEmail;
			} else if(sourceKey !=null && sourceKey.length() != 0) {
				searchPath += "?targetEmail=" + targetEmail + "&sourceKey=" + sourceKey;
			} else {
				searchPath = null;
			}
			
		} else if(targetKey != null && targetKey.length() != 0) {
			
			if(sourceEmail != null && sourceEmail.length() != 0) {
				searchPath += "?targetKey=" + targetKey+ "&sourceEmail=" + sourceEmail;
			} else if(sourceKey !=null && sourceKey.length() != 0) {
				searchPath +=  "?targetKey=" + targetKey + "&sourceKey=" + sourceKey;
			} else {
				searchPath = null;
			}
			
		}
		
		return searchPath;
	}
	
	private String getColleaguePath(String userEmail, String userKey) {
		IRI colleaguePathIRI = new IRI(profileURLs.get("colleague"));
		int port = colleaguePathIRI.getPort();

		String colleaguePath = colleaguePathIRI.getScheme() +"://" + colleaguePathIRI.getHost() + colleaguePathIRI.getPath();
		if (port != -1){
			colleaguePath = colleaguePathIRI.getScheme() +"://" + colleaguePathIRI.getHost() + ":"+port+colleaguePathIRI.getPath();
		}
		
		if(userKey != null && userKey.length() != 0) {
			colleaguePath = colleaguePath + "?connectionType=colleague" + "&key=" + userKey;
		} else if(userEmail != null && userEmail.length() != 0) {
			colleaguePath += "?connectionType=colleague" + "&email=" + userEmail;
		} else {
			colleaguePath = null;
		}
		
		return colleaguePath;
	}
	
	@Override
	protected HashMap<String, String> getCollectionUrls(Service service) {
		HashMap<String, String> collectionUrls = new HashMap<String, String>();
		
		for(Workspace workspace : service.getWorkspaces()) {
			for(Collection collection : workspace.getCollections()) {
				collectionUrls.put(StringConstants.REL_VCARD, collection.getHref().toString());
				
				for(Element editableFieldExtension : collection.getExtension(StringConstants.SNX_EDITABLE_FIELD)) {
					editableFields.put(editableFieldExtension.getAttributeValue(StringConstants.REL_NAME), editableFieldExtension);
				}
			}
			
			for(Element linkExtension : workspace.getExtensions(StringConstants.ATOM_LINK)) {
				// Profiles Service Document does not have name attribute for links
				// Substring the rel attribute to get meaningful names for the links.
				String linkName = linkExtension.getAttributeValue(StringConstants.ATTR_REL);
				linkName = linkName.substring(linkName.lastIndexOf("/") + 1);
				
				collectionUrls.put(linkName, linkExtension.getAttributeValue(StringConstants.ATTR_HREF));
			}
		}
		
		return collectionUrls;
	}
	
	public int getSearchTotalResults(ProfileOutput profileOutput, boolean activeUsersOnly, String city, 
									String state,
									String country, String email, ProfileFormat format, String jobTitle, 
									String name, String organization, int page, int pageSize, 
									String phoneNumber, String profileTags, String profileType, 
									String textSearch, String userid,String invalid) {
		int totalResults = -1; // assume failure
		ExtensibleElement profilesFeed = searchProfiles(service.getServiceURLString() + URLConstants.PROFILES_SEARCH_ALL, profileOutput, activeUsersOnly, city, state, country, email, format, jobTitle, name, organization, page, pageSize, phoneNumber, profileTags, profileType, textSearch, userid,invalid);
		
		if (profilesFeed != null) {
			Element e = profilesFeed.getFirstChild(StringConstants.OPENSEARCH_TOTALRESULTS); 
			if (e != null)
				{
					try {
						totalResults = Integer.parseInt(e.getText());
					} catch (NumberFormatException nfe) {
						totalResults = -1;
					}
				}
			}

		return totalResults;
	}
	
	public ArrayList<Profile> getAllProfiles(ProfileOutput profileOutput, boolean activeUsersOnly, String city, 
											String state,
											String country, String email, ProfileFormat format, String jobTitle, 
											String name, String organization, int page, int pageSize, 
											String phoneNumber, String profileTags, String profileType, 
											String textSearch, String userid,String invalid) {
			ArrayList<Profile> profiles = new ArrayList<Profile>();
			ExtensibleElement profilesFeed = searchProfiles(service.getServiceURLString() + URLConstants.PROFILES_SEARCH_ALL, profileOutput, activeUsersOnly, city, state, country, email, format, jobTitle, name, organization, page, pageSize, phoneNumber, profileTags, profileType, textSearch, userid,invalid);
			
			if (profilesFeed != null) {
				
				// Check for invalid response error and return empty array instead of 
				// getting an exception trying to parse
				Element e = profilesFeed.getExtension(StringConstants.API_RESPONSE_CODE);
				if (e != null) {
					int statusCode = Integer.parseInt(e.getText());
					if (statusCode == 400)
						return profiles;
					if (statusCode == 403)
						return null;
					
				}
				
				for(Entry personEntry: ((Feed) profilesFeed).getEntries()) {
					profiles.add(new Profile(personEntry));
			}
		}
	
	return profiles;
	}

	public ExtensibleElement searchProfiles(String sourceURL, ProfileOutput profileOutput, boolean activeUsersOnly, String city, String state, String country, String email, 
											ProfileFormat format, String jobTitle, String name, String organization, int page, 
											int pageSize, String phoneNumber, String profileTags, String profileType, String textSearch, 
											String userid,String invalid) {
		
		String searchPath = sourceURL;
		
		if(searchPath.lastIndexOf("?") == -1 )
			searchPath += "?";
		else
			searchPath += "&";
		
		if(profileOutput != null)
			searchPath += "output=" + String.valueOf(profileOutput).toLowerCase() + "&";
		
		if(activeUsersOnly)
			searchPath += "activeUsersOnly=" + String.valueOf(activeUsersOnly) + "&";
		
		if(city != null && city.length() != 0)
			searchPath += "city=" + city + "&";
		
		if(state != null && state.length() != 0)
			searchPath += "state=" + state + "&";
		
		if(country != null && country.length() != 0)
			searchPath += "country=" + country + "&";
		
		if(email != null && email.length() != 0)
			searchPath += "email=" + email + "&";
		
		// "lite" or "full" (default == "lite")
		if(format != null)
			searchPath += "format=" + String.valueOf(format).toLowerCase() + "&";
		
		if(jobTitle != null && jobTitle.length() != 0)
			searchPath += "jobTitle=" + jobTitle + "&";
		
		// first, last name from the SURNAME and GIVEN_NAME tables, as well as all names columns in the EMPLOYEE table, 
		// including these columns: 
		// PROF_PREFERRED_FIRST_NAME
		// PROF_PREFERRED_LAST_NAME
		// PROF_NATIVE_FIRST_NAME
		// PROF_NATIVE_LAST_NAME
		// PROF_ALTERNATE_LAST_NAME
		// PROF_DISPLAY_NAME
		if(name != null && name.length() != 0)
			searchPath += "name=" + name + "&";
		
		if(organization != null && organization.length() != 0)
			searchPath += "organization=" + organization + "&";
		
		if(page > 0)
			searchPath += "page=" + page + "&";
		
		// telephoneNumber, ipTelephoneNumber, mobileNumber, pagerNumber
		if(phoneNumber != null && phoneNumber.length() != 0)
			searchPath += "phoneNumber=" + phoneNumber + "&";
		
		// multiple tags can be specified, passed in comma separated
		// with %2C for comma
		if(profileTags != null && profileTags.length() != 0)
			searchPath += "profileTags=" + profileTags + "&";
		
		if(profileType != null)
			searchPath += "profileType=" + String.valueOf(profileType).toLowerCase() + "&";
		
		if(pageSize > 0)
			searchPath += "ps=" + pageSize + "&";

		// Well-formed full text search query. 
		// Performs a text search of the Profile Tags, About Me, and Background fields of all the profiles
		if(textSearch != null && textSearch.length() != 0)
			searchPath += "search=" + textSearch + "&";
		
		// x-lconn-userid
		if(userid != null && userid.length() != 0)
			searchPath += "userid=" + userid + "&";
		
		// invalid will return 'Invalid request' UNLESS other valid parameters are provided
		if(invalid != null)
			searchPath += "invalid=" + String.valueOf(invalid).toLowerCase() + "&";
		
		return getFeed(searchPath);
	}
	
	public VCardEntry vcardFromProfile(Profile profile)	{
		VCardEntry vCard = null;
	
		// Find the vCard content in the profile feed entry.
		vCard = new VCardEntry(profile.getContent(), editableFields);
		
		return vCard;
	}
	
	public String getPeopleManagedAPI(String userWithPeopleManaged) {

		// First we need to get the user's ID, then their service doc as only people
		// with reports have the people-managed request in their docs
		// then retrieve the people-managed API string
		ArrayList<Profile> profilesForID = getAllProfiles(ProfileOutput.VCARD, false, null, null, null, null, ProfileFormat.FULL, null, userWithPeopleManaged, null, 0, 1, null, null, null, null, null, null);
		Profile p = null;
		for(Profile profile : profilesForID) {
			p = profile;
			break;
		}

		String uid = null;
		if (p != null) {
			
			VCardEntry vCard = vcardFromProfile(p);
			LinkedHashMap<String, String> maps = vCard.getVCardFields();
			uid =  maps.get("X_LCONN_USERID");
			if (uid != null) {
				
				ExtensibleElement feed = getFeed(service.getServiceURLString() + URLConstants.PROFILES_SERVICE + "?userid=" + uid);
				if(feed != null) {
					
					HashMap<String, String> thisProfileURLs;
					 
					thisProfileURLs = getCollectionUrls((Service) feed);
					
					if (thisProfileURLs != null) {
						
						return thisProfileURLs.get("people-managed").toString();
					}
				}
			}
		}
		return null;
	}
	
	public ArrayList<Profile> getPeopleManaged(String peopleManagedAPI,int pageNumber,int pageSize) {

		ExtensibleElement profilesFeed = getFeed(peopleManagedAPI + "&ps=" + pageSize + "&page=" + pageNumber);
		if (profilesFeed != null) {
			
			ArrayList<Profile> profiles = new ArrayList<Profile>();
			// Check for invalid response error and return empty array instead of 
			// getting an exception trying to parse
			Element e = profilesFeed.getExtension(StringConstants.API_RESPONSE_CODE);
			if (e != null) {
				int statusCode = Integer.parseInt(e.getText());
				if (statusCode == 400)
					return profiles;
			}
			
			for(Entry personEntry: ((Feed) profilesFeed).getEntries()) {
				profiles.add(new Profile(personEntry));
			}
			return profiles;
		}
		return null;
	}
	
	public Profile getProfile(String sFeedString) {
		Profile profile = null;
		ExtensibleElement profileFeed = getFeed(sFeedString);
		
		if (profileFeed != null) {
			
			// Check for invalid response error and return empty array instead of 
			// getting an exception trying to parse
			Element e = profileFeed.getExtension(StringConstants.API_RESPONSE_CODE);
			if (e != null) {
				int statusCode = Integer.parseInt(e.getText());
				if (statusCode == 400)
					return null;
			}
			
			profile = new Profile((Entry)profileFeed);
		}
	
	return profile;
	}
	
	public ExtensibleElement getSharedConnectionsFeed(String key1, String key2){
		String path = service.getServiceURLString()+ "/atom/connectionsInCommon.do?connectionType=colleague&key="+key1+","+key2;
		return getFeed(path);
	}
	
	public ExtensibleElement getSharedConnectionsFeed(String key1, String key2, String paramsToAppend){
		String path = service.getServiceURLString()+ "/atom/connectionsInCommon.do?connectionType=colleague&key="+key1+","+key2+"&"+paramsToAppend;
		return getFeed(path);
	}
	
	public ExtensibleElement getColleagueFeed(String email,String key){
		return getFeed(getColleaguePath(email,key));
	}
	
	public ExtensibleElement getColleaguesStatusFeed(String email){
		return getFeed(service.getServiceURLString()+"/atom/mv/theboard/entries/related.do?email="+email+"&comment=none");
	}
	
	public boolean removeContact(String link){
		return deleteFeed(link);
	}
	
	public ExtensibleElement acceptInvite(String link, Entry statusEntry){
		return putFeed(link,statusEntry);
	}
	
	public Profile getEditFeed(Entry colleagueStatusEntry) {
		return getProfile(colleagueStatusEntry.getLink(StringConstants.REL_EDIT).getHref().toString());
	}
	
	public Profile  getSelfFeed(Entry colleagueStatusEntry) {
		return getProfile(colleagueStatusEntry.getLink(StringConstants.REL_SELF).getHref().toString());
	}
	
	public ExtensibleElement getProfileServiceConfigurations(){
		return getFeed(service.getServiceURLString() + "/serviceconfigs");
	}
	
	public ExtensibleElement getProfileBuzzFile(String email){
		return getFeed(service.getServiceURLString() + "/atom/mv/thebuzz/entry/status.do?email=" + email);
	}
	
	public ExtensibleElement getProfilePhoto(String key){
		return getFeed(service.getServiceURLString() + "/photo.do?key=" + key);
	}
	
	public ExtensibleElement createFollow(String targetUser, String currentUser, Profile profile){
		return postFeed(service.getServiceURLString() + "/html/following.do?targetKey=" + targetUser + "&sourceKey=" + currentUser + "&action=follow", profile.toEntry());
	}
	
	public ExtensibleElement createFollow(Entry entry){
		// TJB 11/21/14 This is the supported API to establish follow relationship - not /html/following.do
		return postFeed(service.getServiceURLString() + "/follow/atom/resources?source=profiles&type=profile", entry);
	}
	
	public ExtensibleElement deleteFollow(String targetUser, String currentUser, Profile profile){
		return postFeed(service.getServiceURLString() + "/html/following.do?targetKey=" + targetUser + "&sourceKey=" + currentUser + "&action=unfollow", profile.toEntry());
	}
	
	public boolean deleteFollow(String url){
		return deleteFeed(url);
	}
	
	public ExtensibleElement getFollow(){
		return getFeed(service.getServiceURLString() + "/follow/atom/resources?source=profiles&type=profile");
	}
	
	/**
	 * Gets the report-to chain for the specified user. Only one of the parameters is required;
	 * The rest can be passed in as null
	 * @param userEmail
	 * @param userId
	 * @param profileKey
	 * @return Feed of the user's report-to chain
	 */
	public ExtensibleElement getReportingChain(String userEmail, String userId, String profileKey){
		String url = service.getServiceURLString() + URLConstants.PROFILES_SEARCH_REPORT_CHAIN;
		if (userId != null)
			url += "userid=" + userId;
		else if (profileKey != null)
			url += "key=" + profileKey;
		else if (userEmail != null)
			url += "email=" + userEmail;
		
		return getFeed(url);
	}
	
	/**
	 * Gets the profile feed for the specified user. Only one of the parameters is required;
	 * The rest can be passed in as null
	 * @param userEmail
	 * @param userId
	 * @param profileKey
	 * @return Feed of the user's profile
	 */
	public ExtensibleElement getProfileFeed(String userEmail, String userId, String profileKey){
		String url = service.getServiceURLString() + URLConstants.PROFILES_SEARCH_PROFILES;
		if (userId != null)
			url += "userid=" + userId;
		else if (profileKey != null)
			url += "key=" + profileKey;
		else if (userEmail != null)
			url += "email=" + userEmail;
		
		return getFeed(url);
	}
	
	/**
	 * Legacy Endpoint, not used anymore
	 * @param userEmail
	 * @return
	 */
	public ExtensibleElement getCurrentStatus(String userEmail){
		return getFeed(service.getServiceURLString() + URLConstants.PROFILES_LEGACY_STATUS + "?email="+userEmail);
	}
	
	/**
	 * Legacy endpoint - UI is unnaffected since the function it performs has been abandoned
	 * @param userEmail
	 * @return
	 */
	public boolean deleteStatuses(String userEmail) {
		return deleteFeed(service.getServiceURLString() + URLConstants.PROFILES_LEGACY_STATUS + "?email="+userEmail);
	}
	
	public ExtensibleElement uploadProfilePicture(String profPicLink, InputStream fileInputStream, String fileMimeType) throws FileNotFoundException{
		return putFileWithStream(profPicLink, fileInputStream, fileMimeType);
	}
	
	public ExtensibleElement uploadPronunciationFile(String pronunciationAudioLink, InputStream audioInputStream, String fileMimeType) throws FileNotFoundException{
		return putFileWithStream(pronunciationAudioLink, audioInputStream, fileMimeType);
	}
	
	public ExtensibleElement getAnyFeed(String url){
		return getFeed(url);
	}
	
	public boolean deleteAnyFeed(String url){
		return deleteFeed(url);
	}
	
	/* Method below may need to be refactored due to duplication
	 * ActivityStreamService contains similar method for posting Json entries
	 * Activity Stream API Handler class may need to be created to avoid duplication
	 */
	public String createASEntry ( String uri, String entrySt ) {
		
		return postResponseJSONString(uri, entrySt);
	}
	public String getServiceURLString(){
		
		return service.getServiceURLString().toString();
		
		
	}
	public ExtensibleElement postFeed(String URL, Entry entry){
		
		
		return super.postFeed(URL, entry);
		
	}

	public ExtensibleElement getFollowedProfiles() {
		String followURL = service.getServiceURLString() + "/follow/atom/resources?source=PROFILES&type=PROFILE&ps=100";
		return getFeed(followURL);
	}
}