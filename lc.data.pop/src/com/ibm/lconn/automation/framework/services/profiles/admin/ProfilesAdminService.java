package com.ibm.lconn.automation.framework.services.profiles.admin;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.profiles.admin.nodes.Profile;
import com.ibm.lconn.automation.framework.services.profiles.nodes.ProfilePerspective;
import com.ibm.lconn.automation.framework.services.profiles.nodes.TagsEntry;

public class ProfilesAdminService extends LCService {
	
	private HashMap<String, String> profilesAdminURLs;
	private HashMap<String, Element> adminEditableFields;
	protected final static Logger LOGGER = LoggerFactory.getLogger(ProfilesAdminService.class.getName());
	
	public ProfilesAdminService(AbderaClient client, ServiceEntry service) throws LCServiceException {
		this(client, service, new HashMap<String, String>());
	}
	
	public ProfilesAdminService(AbderaClient client, ServiceEntry service, Map<String, String> headers) throws LCServiceException {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}	
		
		adminEditableFields = new HashMap<String, Element>();
		
		// if ((StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			ExtensibleElement feed = getFeed(service.getServiceURLString() + URLConstants.PROFILES_ADMIN_SERVICE);
			assertEquals("Can't get profiles service doc", 200, getRespStatus());
		
			if(feed != null) {
				setFoundService(true);
				profilesAdminURLs = getCollectionUrls((Service) feed);
			} else {
				setFoundService(false);
				throw new LCServiceException("Error : Can't get ProfilesAdminService Feed");
			}
		// }
	}
	
	public ArrayList<Profile> getAllProfiles(String email, String key, int pageSize, String uid, String userid) {
		ArrayList<Profile> profiles = new ArrayList<Profile>();
		ExtensibleElement profilesFeed = getAllUsersFeed(email,key,pageSize,uid,userid);
		
		if (profilesFeed != null) {
		
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
			}
		
		return profiles;
		}

	public ExtensibleElement getAllUsersFeed(String email, String key, int pageSize, String uid, String userid) {

		String searchPath = profilesAdminURLs.get(StringConstants.PROFILES_ADMIN_ALL_USERS);
		
		if(searchPath.lastIndexOf("?") == -1 )
			searchPath += "?";
		else
			searchPath += "&";

		if(uid != null && uid.length() != 0)
			searchPath += "uid=" + uid + "&";
		else if(userid != null && userid.length() != 0)
			searchPath += "userid=" + userid + "&";
		else if(key != null && key.length() != 0)
			searchPath += "key=" + key + "&";
		else if(email != null && email.length() != 0)
			searchPath += "email=" + email + "&";
		else
		  LOGGER.error("ALL PARAMETERS ARE NULL,  WILL RETURN RANDOM USER PROFILE" );

		if(pageSize > 0)
			searchPath += "ps=" + pageSize + "&";

		
		return getFeed(searchPath);
	}
	
	@Override
	protected HashMap<String, String> getCollectionUrls(Service service) {
		HashMap<String, String> collectionUrls = new HashMap<String, String>();
		
		for(Workspace workspace : service.getWorkspaces()) {
			for(Collection collection : workspace.getCollections()) {
				collectionUrls.put(StringConstants.PROFILES_ADMIN_ALL_USERS, collection.getHref().toString());
				
				for(Element editableFieldExtension : collection.getExtension(StringConstants.SNX_EDITABLE_FIELD)) {
					adminEditableFields.put(editableFieldExtension.getAttributeValue(StringConstants.REL_NAME), editableFieldExtension);
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

	public HashMap<String, Element> getEditableFields() {
		return adminEditableFields;
	}
	
	public int createProfile(String sUserID,Profile profile) {
		
		int statusCode = 0;
		ExtensibleElement result = postFeed(profilesAdminURLs.get(StringConstants.PROFILES_ADMIN_ALL_USERS) + "?userid=" + sUserID,profile.toEntry());
		if (result != null) {
			
			// Check for invalid response error and return empty array instead of 
			// getting an exception trying to parse
			Element e = result.getExtension(StringConstants.API_RESPONSE_CODE);
			if (e != null) {
				statusCode = Integer.parseInt(e.getText());
				}
			else
				statusCode = 200;
		}
		
		return statusCode;
	
	}

	public int updateProfile(Profile profile,String email, String key, String uid, String userid) {

		String sEditFeed = getFirstProfileEditFeed(email,key,uid,userid);
		if (sEditFeed == null)
			return 0;
		
		int statusCode = 0;
		ExtensibleElement result = putFeed(sEditFeed,profile.toEntry());
		if (result != null) {
			
			// Check for invalid response error and return empty array instead of 
			// getting an exception trying to parse
			Element e = result.getExtension(StringConstants.API_RESPONSE_CODE);
			if (e != null) {
				statusCode = Integer.parseInt(e.getText());
				}
			else
				statusCode = 200;
		}
		
		return statusCode;
	
	}

	private Entry getFirstMatch(String email, String key, String uid, String userid) {
		
		ExtensibleElement profilesFeed = getAllUsersFeed(email,key,1,uid,userid);
		if (profilesFeed == null)
			return null;
		
		// Check for invalid response error and return empty array instead of 
		// getting an exception trying to parse
		Element e = profilesFeed.getExtension(StringConstants.API_RESPONSE_CODE);
		if (e != null) {
			int statusCode = Integer.parseInt(e.getText());
			if (statusCode == 400)
				return null;
			}
			
		List<Entry> le = ((Feed)profilesFeed).getEntries();
		if (le.size() < 1)
			return null;
		
		return le.get(0); 
	}

	public Profile getFirstProfile(String email, String key,String uid, String userid) {
	
		LOGGER.debug("getFirstProfile: email = " +email +", key = " +key +", userid = " +userid);
		LOGGER.debug("getFirstProfile: profilesAdminURLs: "  +profilesAdminURLs);
		
		Entry entry = getFirstMatch(email,key,uid,userid);
		if (entry == null)
			return null;
		
		return new Profile(entry); 
	}

	private String getFirstProfileEditFeed(String email, String key,String uid, String userid) {
		
		Entry entry = getFirstMatch(email,key,uid,userid);
		if (entry == null)
			return null;
		
		return entry.getLink(StringConstants.REL_EDIT).getHref().toString();
	}

	public boolean deleteProfile(String email, String key,String uid, String userid) {

		String sEditFeed = getFirstProfileEditFeed(email,key,uid,userid);
		if (sEditFeed != null) 
			// TODO:  if profile not exist, do nothing now, need add profile
		{
			try {
				adminDoDelete(sEditFeed);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
		
	}
	
	private String getTagSearchPath(String targetEmail, String targetKey,boolean bFull,String sourceEmail,String sourceKey) {
		
		String sBaseURL = service.getServiceURLString();
		
		String searchPath = sBaseURL + "/admin/atom/profileTags.do";
		
		if(targetEmail != null && targetEmail.length() != 0) {
			searchPath += "?targetEmail=" + targetEmail;
		} else if(targetKey != null && targetKey.length() != 0) {
			searchPath += "?targetKey=" + targetKey;
		} else {
			return null;
		}
		
		if(bFull)
			searchPath += "&format=full";
		
		if(sourceEmail != null && sourceEmail.length() != 0) {
			searchPath += "&sourceEmail=" + sourceEmail;
		} else if(sourceKey != null && sourceKey.length() != 0) {
			searchPath += "&sourceKey=" + sourceKey;
		}
		
		return searchPath;
	}
	
	private String getTagPostPath(String targetEmail, String targetKey, String sourceEmail, String sourceKey) {
		String sBaseURL = service.getServiceURLString();
		
		String searchPath = sBaseURL + "/admin/atom/profileTags.do";
		
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
	
	public Categories getProfileTags(String targetEmail, String targetKey,boolean bFull,String sourceEmail,String sourceKey) {
		Categories categories = null;
		String searchPath = getTagSearchPath(targetEmail, targetKey,bFull,sourceEmail,sourceKey);
		
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

	private String getPath(String sCommand) {
		String sBaseURL = service.getServiceURLString();
		
		String searchPath = sBaseURL + sCommand;

		return searchPath;
	}
	
	private String getAdminPath(String sCommand,String targetEmail, String targetKey, String targetUserid, String sourceEmail, String sourceKey,String sourceUserid) {
		String sBaseURL = service.getServiceURLString();
		
		String searchPath = sBaseURL + sCommand;
		
		if (targetUserid != null && targetUserid.length() != 0)
			searchPath += "targetUserid=" + targetUserid + "&";
		else if (targetKey != null && targetKey.length() != 0)
			searchPath += "targetKey=" + targetKey + "&";
		else if (targetEmail != null && targetEmail.length() != 0)
			searchPath += "targetEmail=" + targetEmail + "&";
		else
			return null;
		
		if (sourceUserid != null && sourceUserid.length() != 0)
			searchPath += "sourceUserid=" + sourceUserid;
		else if (sourceKey != null && sourceKey.length() != 0)
			searchPath += "sourceKey=" + sourceKey;
		else if (sourceEmail != null && sourceEmail.length() != 0)
			searchPath += "sourceEmail=" + sourceEmail;
		else
			return null;
		
		return searchPath;
	}
	
	public int createColleagueConnection(String targetEmail, String targetKey, String targetUserid, String sourceEmail, String sourceKey,String sourceUserid) {
		int statusCode = 0;
		String putPath = getAdminPath("/admin/atom/connections.do?action=complete&",targetEmail, targetKey, targetUserid,sourceEmail, sourceKey,sourceUserid);
		
		if(putPath != null) {
			// This doesn't take any data in the put, to keep things simple we send a dummy one
			Factory factory = Abdera.getNewFactory();
			ExtensibleElement ignoredPutData = factory.newElement(new QName("http://ns.opensocial.org/2008/opensocial", "person"));
			ExtensibleElement feed = putFeed(putPath,ignoredPutData);
			if (feed != null) {
				
				// Check for invalid response error and return empty array instead of 
				// getting an exception trying to parse
				Element e = feed.getExtension(StringConstants.API_RESPONSE_CODE);
				if (e != null) {
					statusCode = Integer.parseInt(e.getText());
					}
				else
					statusCode = 200;
			}
			
		}

		return statusCode;
	}
	
	public boolean deleteColleagueConnection(String targetEmail, String targetKey, String targetUserid, String sourceEmail, String sourceKey,String sourceUserid) {

		String delPath = getAdminPath("/admin/atom/connections.do?",targetEmail, targetKey, targetUserid,sourceEmail, sourceKey,sourceUserid);
		
		if(delPath != null) {
			try {
				adminDoDelete(delPath);
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		return false;
	}
	
	public int createFollowColleague(String targetEmail, String targetKey, String targetUserid, String sourceEmail, String sourceKey,String sourceUserid) {
		int statusCode = 0;
		String putPath = getAdminPath("/admin/atom/following.do?action=follow&",targetEmail, targetKey, targetUserid,sourceEmail, sourceKey,sourceUserid);
		
		if(putPath != null) {
			// This doesn't take any data in the put, to keep things simple we send a dummy one
			Factory factory = Abdera.getNewFactory();
			ExtensibleElement ignoredPutData = factory.newElement(new QName("http://ns.opensocial.org/2008/opensocial", "person"));
			ExtensibleElement feed = putFeed(putPath,ignoredPutData);
			if (feed != null) {
				
				// Check for invalid response error and return empty array instead of 
				// getting an exception trying to parse
				Element e = feed.getExtension(StringConstants.API_RESPONSE_CODE);
				if (e != null) {
					statusCode = Integer.parseInt(e.getText());
					}
				else
					statusCode = 200;
			}
			
		}

		return statusCode;
	}
	
	public boolean deleteFollowColleague(String targetEmail, String targetKey, String targetUserid, String sourceEmail, String sourceKey,String sourceUserid) {

		String delPath = getAdminPath("/admin/atom/following.do?action=unfollow&",targetEmail, targetKey, targetUserid,sourceEmail, sourceKey,sourceUserid);
		
		if(delPath != null) {
			try {
				adminDoDelete(delPath);
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		return false;
	}
	
	public int searchProfilesNonAdmin(String name,boolean activeUsersOnly) {
		
		String searchPath = service.getServiceURLString() + "/atom/search.do?ps=100&";
		
		if (name != null && name.length() != 0)
			searchPath += "name=" + name + "&";

		if (activeUsersOnly)
			searchPath += "activeUsersOnly=true&";
		else
			searchPath += "activeUsersOnly=false&";
		
		ExtensibleElement profilesFeed = getFeed(searchPath);
		assertEquals("get Search result", 200, this.getRespStatus());
		
		LOGGER.debug("activeUsers is "+activeUsersOnly+" : "+ ((Feed) profilesFeed).getEntries().size());
		return ((Feed) profilesFeed).getEntries().size();
	}
	

	public int getNumberOfConnections(String key) {
		
		String searchPath = service.getServiceURLString() + URLConstants.PROFILES_SEARCH_CONNECTIONS_LIST +  "&key=" + key + "&status=accepted";
		
		ExtensibleElement profilesFeed = getFeed(searchPath);

		int totalResults = -1;
		
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

	public HashMap<String, String> getMapValues(String sRelativeFeed) {
		
		String searchPath = getPath(sRelativeFeed);
		
		if(searchPath == null)
			return null;
		
		ExtensibleElement feed = getFeed(searchPath);

		if (feed == null)
			return null;
			
		// Check for invalid response error
		Element e = feed.getExtension(StringConstants.API_RESPONSE_CODE);
		if (e != null) {
			int statusCode = Integer.parseInt(e.getText());
			if (statusCode == 400)
				return null;
		}
		
		HashMap<String,String> mapValues = new HashMap<String, String>();
		for(Entry personEntry: ((Feed) feed).getEntries()) {
			
			Content content = personEntry.getContentElement();
			
			Element element = content.getFirstChild().getFirstChild();
			
			String sKey = element.getText();
			
			element = element.getNextSibling();
			String sValue = element.getText();
			
			mapValues.put(sKey, sValue);
			
		}
		return mapValues;
	}
	
	public String getUserID(String name) {
		
		String searchPath = service.getServiceURLString() + "/atom/search.do?ps=200&";
		
		searchPath += "name=" + name + "&";
		searchPath += "activeUsersOnly=false&";
		
		ExtensibleElement profilesFeed = getFeed(searchPath);

		if (profilesFeed == null)
		    return null;
		
		// Check for invalid response error 
		Element e = profilesFeed.getExtension(StringConstants.API_RESPONSE_CODE);
		if (e != null) {
			int statusCode = Integer.parseInt(e.getText());
			if (statusCode > 200)
				return null;
			}
		
		List<Entry> entries = ((Feed)profilesFeed).getEntries();
		if (entries.size() < 1)
			return null;

		// Find the exact match to name
		String sUserID = null;
		for (Entry entry : entries) {
			try {
				if (name.equalsIgnoreCase(URLEncoder.encode(entry.getTitle(), "UTF-8"))) {
					sUserID = entry.getContributors().get(0).getFirstChild(StringConstants.SNX_USERID).getText();
					break;
				}
			} catch (UnsupportedEncodingException uee) { 
				// ignore this entry if we can't encode it
			}
		}
		
		return sUserID;
	}
	

	
	/*
	 * Performs Delete.
	 * 
	 * url - correctly formed url
	 * 
	 */
	public void adminDoDelete(String url) throws Exception{
		ProfilePerspective admin = new ProfilePerspective(0, true);
		admin.getService().deleteUrlFeed(url);

	}	
	
	public ExtensibleElement getExtensibleElement(String url) {
		return getFeed(url);
	}
	
	public ExtensibleElement postAdminFeed(String url, ExtensibleElement feed) {
		return postFeed(url, feed);
	}
	
	public ExtensibleElement postEntry(String url, Entry ntry) {
		return super.postFeed(url, ntry);
	}
	
	public ExtensibleElement putAdminFeed(String url, ExtensibleElement feed) {
		return putFeed(url, feed);
	}
	
	public ExtensibleElement putEntry(String url, Entry ntry){
		return super.putFeed(url, ntry);
	}
	
	public boolean delete(String url){
		return deleteFeed(url);
	}

	public ExtensibleElement postFollowFeedForProfile(Feed feed, String uuid) {
		String followURL = getServiceURLString() + "/follow/atom/resources?source=PROFILES&type=PROFILE&resource=" + uuid;
		return postFeed(followURL, feed);
	}

	public void deleteFollowFeedForProfile(Feed feed, String uuid) throws Exception {
		String followURL = getServiceURLString() + "/follow/atom/resources?source=PROFILES&type=PROFILE&resource=" + uuid;
		deleteFeedWithBody(followURL, feed.toString());
	}
	public ExtensibleElement getFollowedProfiles() {
		String followURL = service.getServiceURLString() + "/follow/atom/resources?source=PROFILES&type=PROFILE&ps=100";
		return getFeed(followURL);
	}

}
