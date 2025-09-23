package com.ibm.lconn.automation.framework.services.dogear;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Service;
import org.apache.abdera.protocol.client.AbderaClient;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Access;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Format;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Network;
import com.ibm.lconn.automation.framework.services.common.StringConstants.PopularBookmarksSort;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SearchOperator;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Sort;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;

/**
 * Profiles Service object handles getting/posting data to the Connections Profiles service.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class DogearService extends LCService {
	
	private HashMap<String, String> dogearURLs;
	
	
	public DogearService(AbderaClient client, ServiceEntry service) throws LCServiceException{
		this(client, service, new HashMap<String, String>());
	}
	
	public DogearService(AbderaClient client, ServiceEntry service, Map<String, String> headers) throws LCServiceException {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}
		
		ExtensibleElement feed = getFeed(service.getServiceURLString() + URLConstants.DOGEAR_SERVICE);		
		if(feed != null) {
			if(getRespStatus() == 200) {
				setFoundService(true);
				dogearURLs = getCollectionUrls((Service) feed);
			} else {
				setFoundService(false);
				throw new LCServiceException("Error : Can't get DogearService Feed");
			}
		} else {
			setFoundService(false);
			throw new LCServiceException("Error : Can't get DogearService Feed");
		}
	}
	
	
	public ExtensibleElement createBookmark(Bookmark newBookmark) {
		return postFeed(dogearURLs.get(StringConstants.DOGEAR_ENTRIES).toString(), newBookmark.toEntry());
	}
	
	public ExtensibleElement sendBookmark(Entry notificationEntry){
		return postFeed(dogearURLs.get(StringConstants.DOGEAR_SEND_NOTIFICATIONS).toString(), notificationEntry);
	}
	
	public ExtensibleElement editBookmark(String bookmarkEditHref, Bookmark updatedBookmark) {
		return putFeed(bookmarkEditHref, updatedBookmark.toEntry());
	}
	
	public ExtensibleElement getBookmark(String bookmarkEditHref) {
		return getFeed(bookmarkEditHref);
	}
	
	public boolean deleteBookmark(String bookmarkEditHref) {
		return deleteFeed(bookmarkEditHref);
	}
	
	public ExtensibleElement getMyBookmarks() {
		return getFeed(dogearURLs.get(StringConstants.DOGEAR_ENTRIES).toString());
	}
	
	public ExtensibleElement getPublicBookmarks(){
		return getFeed(service.getServiceURLString() + URLConstants.DOGEAR_SEARCH);
	}
	
	public ExtensibleElement getBookmarkTags(Access access, String base, String email, Format format, Network network, Date since, String tag, String url, String userid) {
		return searchBookmarks(service.getServiceURLString() + URLConstants.DOGEAR_TAGS, access, base, email, format, null, network, 0, 0, null, null, false, since, null, null, tag, url, userid, null);
	}
	
	public ExtensibleElement getPopularBookmarks(Lang lang, int page, int pageSize, PopularBookmarksSort type) {
		return searchBookmarks(service.getServiceURLString() + URLConstants.DOGEAR_POPULAR, null, null, null, null, lang, null, page, pageSize, null, null, false, null, null, null, null, null, null, type);
	}
	
	public ExtensibleElement getNotifiedBookmarks(Lang lang, int page, int pageSize) {
		return searchBookmarks(service.getServiceURLString() + URLConstants.DOGEAR_MY_NOTIFICATIONS, null, null, null, null, lang, null, page, pageSize, null, null, false, null, null, null, null, null, null, null);
	}
	
	public ExtensibleElement getSentBookmarks(Lang lang, int page, int pageSize) {
		return searchBookmarks(service.getServiceURLString() + URLConstants.DOGEAR_SENT_NOTIFICATIONS, null, null, null, null, lang, null, page, pageSize, null, null, false, null, null, null, null, null, null, null);
	}
	
	public ExtensibleElement searchBookmarks(String sourceURL, Access access, String base, String email, Format format, Lang lang, 
			Network network, int page, int pageSize, String search, SearchOperator searchOperator, boolean showFavIcon, 
			Date since, Sort sort, SortOrder sortOrder, String tag, String url, String userid, PopularBookmarksSort popularSort) {
		
		String searchPath = sourceURL;
		
		if(sourceURL != null)
			searchPath = sourceURL + "?";
		else
			searchPath = service.getServiceURLString() + URLConstants.DOGEAR_SEARCH + "?";
		
		if(searchPath.lastIndexOf("?") == -1 )
			searchPath += "?";
		else
			searchPath += "&";
		
		if(access != null)
			searchPath += "access=" + access + "&";
		
		if(base != null && base.length() != 0)
			searchPath += "base=" + base + "&";
		
		if(email != null && email.length() != 0)
			searchPath += "email=" + email + "&";
		
		if(format != null)
			searchPath += "format=" + format.toString().toLowerCase() + "&";
		
		if(lang != null)
			searchPath += "lang=" + lang.toString() + "&";
		
		if(network != null)
			searchPath += "network=" + network.toString().toLowerCase() + "&";
		
		if(page > 0)
			searchPath += "page=" + page + "&";
		
		if(pageSize > 0)
			searchPath += "ps=" + pageSize + "&";
		
		if(search != null && search.length() != 0)
			searchPath += "search=" + search + "&";
		
		if(searchOperator != null)
			searchPath += "searchOperator=" + searchOperator.toString().toLowerCase() + "&";
		
		if(showFavIcon)
			searchPath += "showFavIcon=" + showFavIcon + "&";
		
		if(since != null)
			searchPath += "since=" + Utils.dateFormatter.format(since) + "&";
		
		if(sort != null)
			searchPath += "sort=" + sort.toString().toLowerCase() + "&";
		
		if(sortOrder != null)
			searchPath += "sortOrder=" + sortOrder.toString().toLowerCase() + "&";
		
		if(tag != null && tag.length() != 0) {
			searchPath += "tag=" + tag + "&";
		}
		
		if(url != null && url.length() != 0)
			searchPath += "url=" + url + "&";
		
		if(userid != null && userid.length() != 0)
			searchPath += "userid=" + userid + "&";
		
		if(popularSort != null)
			searchPath += "type=" + popularSort.toString().toLowerCase() + "&";
		
		return getFeed(searchPath);
	}
	
	public ExtensibleElement getFeedWithRedirect(String url) {
		return super.getFeedWithRedirect(url);
	}
}