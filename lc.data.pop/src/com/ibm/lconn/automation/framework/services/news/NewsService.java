package com.ibm.lconn.automation.framework.services.news;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
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
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;

/**
 * News Service object handles getting/posting data to the Connections News service.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class NewsService extends LCService {

	private HashMap<String, String> newsDashboardURLs;
	
	/**
	 * Constructor to create a new News Service helper object. 
	 * 
	 * This object contains helper methods for all API calls that are supported by the News service.
	 * 
	 * @param client	the authenticated AbderaClient that is used to handle requests to/from server
	 * @param service	the ServiceEntry that contains information about the News service from the server ServiceConfigs news
	 * @throws LCServiceException 
	 */
	public NewsService(AbderaClient client, ServiceEntry service) throws LCServiceException {
		super(client, service);
		
		updateServiceDocument();
	}
	
	public NewsService(AbderaClient client, ServiceEntry service, Map<String, String> headers) throws LCServiceException {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}		
		
		updateServiceDocument();
	}
	
	private void updateServiceDocument() throws LCServiceException {
		ExtensibleElement feed = getFeed(service.getServiceURLString() + URLConstants.NEWS_SERVICE);
		
		if(feed != null) {
			if(getRespStatus() == 200) {
				setFoundService(true);
				newsDashboardURLs = getNewsCollectionUrls((Service) feed);
			} else {
				setFoundService(false);
				throw new LCServiceException("Error : Can't get NewsService Feed, status: " + getRespStatus());
			}
		} else {
			setFoundService(false);
			throw new LCServiceException("Error : Can't get NewsService Feed, status: " + getRespStatus());
		}
	}
	
	protected HashMap<String, String> getNewsCollectionUrls(Service service) {
		HashMap<String, String> collectionUrls = new HashMap<String, String>();
		
		for(Workspace workspace : service.getWorkspaces()) {
			for(Collection collection : workspace.getCollections()) {
				Category collectionType = (Category)collection.getExtension(StringConstants.ATOM_CATEGORY);
				collectionUrls.put(collectionType.getTerm(), collection.getHref().toString());
				getApiLogger().debug(collectionUrls.get(collectionType.getTerm()).toString());
			}
		}
		return collectionUrls;
	}

	public ArrayList<Entry> getAllPublicUpdates(Date before, String containerId, String email, Lang lang, int page, int pageSize, Date since, Component source, String userid) {
		ArrayList<Entry> updates = new ArrayList<Entry>();
		ExtensibleElement publicUpdatesFeed = searchNews(newsDashboardURLs.get(StringConstants.NEWS_DISCOVERY), before, containerId, email, lang, page, pageSize, since, source, userid, null, null);
		
		if (publicUpdatesFeed != null) {
			for(Entry updateEntry: ((Feed) publicUpdatesFeed).getEntries()) {
				updates.add(updateEntry);
			}
		}
		return updates;
	}

	public ArrayList<Entry> getSavedUpdates(Date before, String containerId, String email, Lang lang, int page, int pageSize, Date since, Component source, String userid) {
		ArrayList<Entry> updates = new ArrayList<Entry>();
		ExtensibleElement savedUpdates = searchNews(newsDashboardURLs.get(StringConstants.NEWS_SAVED), before, containerId, email, lang, page, pageSize, since, source, userid, null, null);
		
		if (savedUpdates != null) {
			for(Entry updateEntry: ((Feed) savedUpdates).getEntries()) {
				updates.add(updateEntry);
			}
		}
		return updates;
	}
	
	public ArrayList<Entry> getStatusUpdates(Date before, String containerId, String email, Lang lang, int page, int pageSize, Date since, Component source, String comments) {
		ArrayList<Entry> updates = new ArrayList<Entry>();
		ExtensibleElement status = searchNews(service.getServiceURLString() + URLConstants.NEWS_STATUS_UPDATES, before, containerId, email, lang, page, pageSize, since, source, null, null, comments);
		
		if (status != null) {
			for(Entry updateEntry: ((Feed) status).getEntries()) {
				updates.add(updateEntry);
			}
		}
		return updates;
	}
	
	public ArrayList<Entry> getTopUpdates(Date before, String containerId, String email, Lang lang, int page, int pageSize, Date since, Component source, String userid) {
		ArrayList<Entry> updates = new ArrayList<Entry>();
		ExtensibleElement topUpdatesFeed = searchNews(newsDashboardURLs.get(StringConstants.NEWS_PERSON_TOP), before, containerId, email, lang, page, pageSize, since, source, userid, null, null);
		
		if (topUpdatesFeed != null) {
			for(Entry updateEntry: ((Feed) topUpdatesFeed).getEntries()) {
				updates.add(updateEntry);
			}
		}
		return updates;
	}
	
	public ArrayList<Entry> getPersonFeedUpdates(Date before, String containerId, String email, Lang lang, int page, int pageSize, Date since, Component source, String userid) {
		ArrayList<Entry> updates = new ArrayList<Entry>();
		ExtensibleElement personUpdatesFeed = searchNews(service.getServiceURLString() + URLConstants.NEWS_PERSON_UPDATES, before, containerId, email, lang, page, pageSize, since, source, userid, null, null);
		
		if (personUpdatesFeed != null) {
			for(Entry updateEntry: ((Feed) personUpdatesFeed).getEntries()) {
				updates.add(updateEntry);
			}
		}
		return updates;
	}
	
	public ArrayList<Entry> getCommunityUpdates(Date before, Lang lang, int page, int pageSize, Date since, String communityUUID) {
		ArrayList<Entry> updates = new ArrayList<Entry>();
		ExtensibleElement communityUpdates = searchNews(service.getServiceURLString() + URLConstants.NEWS_COMMUNITY_UPDATES, before, null, null, lang, page, pageSize, since, null, null, communityUUID, null);
		
		if (communityUpdates != null) {
			for(Entry updateEntry: ((Feed) communityUpdates).getEntries()) {
				updates.add(updateEntry);
			}
		}
		return updates;
	}
	
	public ArrayList<Entry> getNewsFeedUpdates(Date before, String containerId, String email, Lang lang, int page, int pageSize, Date since, Component source, String userid) {
		ArrayList<Entry> updates = new ArrayList<Entry>();
		ExtensibleElement personUpdatesFeed = searchNews(newsDashboardURLs.get(StringConstants.NEWS_PERSON_FEED), before, containerId, email, lang, page, pageSize, since, source, userid, null, null);
		
		if (personUpdatesFeed != null) {
			for(Entry updateEntry: ((Feed) personUpdatesFeed).getEntries()) {
				updates.add(updateEntry);
			}
		}
		return updates;
	}
	
	public boolean saveNewsStory(String storyId) {
		Entry saveStoryEntry = Abdera.getNewFactory().newEntry();
		saveStoryEntry.setId(storyId);
		ExtensibleElement result = postFeed(newsDashboardURLs.get(StringConstants.NEWS_SAVED), saveStoryEntry);
		
		if(result != null)
			return true;
		
		return false;
	}
	
	public ExtensibleElement getNewsProfilesStories() {
		return getFeed(newsDashboardURLs.get(StringConstants.NEWS_PERSON_TOP)+"?source=profiles");
	}
	
	public boolean removeSavedNewsStory(String savedStoryEditHref) {
		return deleteFeed(savedStoryEditHref);
	}
	
	public ExtensibleElement searchNews(String sourceURL, Date before, String containerId, String email, Lang lang, int page, int pageSize, Date since, Component source, String userid, String communityUUID, String comments) {
		String searchPath = sourceURL;
		
		if(searchPath.lastIndexOf("?") == -1 )
			searchPath += "?";
		else
			searchPath += "&";
		
		if(before != null)
			searchPath += "before=" + Utils.dateFormatter.format(before) + "&";
		
		if(source != null) {
			searchPath += "source=" + String.valueOf(source).toLowerCase() + "&";
			
			if(containerId != null && containerId.length() > 0) {
				searchPath += "container=" + containerId + "&";
			}
		}
		
		if(lang != null)
			searchPath += "lang=" + lang.toString() + "&";
		
		if(email != null && email.length() != 0)
			searchPath += "email=" + email + "&";
		
		if(page > 0)
			searchPath += "page=" + page + "&";
		
		if(pageSize > 0)
			searchPath += "ps=" + pageSize + "&";
		
		if(since != null)
			searchPath += "since=" + Utils.dateFormatter.format(since) + "&";
		
		if(userid != null && userid.length() != 0)
			searchPath += "userid=" + userid + "&";
		
		if(communityUUID != null && communityUUID.length() != 0)
			searchPath += "communityUUID=" + communityUUID + "&";
		
		if(comments != null)
			searchPath += "comments=" + comments + "&";
		
		return getFeed(searchPath);
	}

	public ExtensibleElement getNewsFeed(String url){
		return getFeed(url);
	}
	
}
	