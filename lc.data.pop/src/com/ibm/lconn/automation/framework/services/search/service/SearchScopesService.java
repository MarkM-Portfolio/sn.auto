package com.ibm.lconn.automation.framework.services.search.service;

import java.util.ArrayList;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.search.data.SearchScope;

public class SearchScopesService extends LCService {
	
	public SearchScopesService(AbderaClient client, ServiceEntry service) {
		super(client, service);
		
		if(service != null)
			this.setFoundService(true);
	}
	
	public ArrayList<SearchScope> getAllScopes() {
		return getScopes(null);
	}

	public SearchScope getScope(String scopeId) {
		ArrayList<SearchScope> results = getScopes(scopeId);
		if (results.size() >= 1){
			return results.get(0);
		}
		return null;
	}
	
	private ArrayList<SearchScope> getScopes(String scopeId) {
		String scopesPath = service.getServiceURLString() + URLConstants.SCOPES;
		ArrayList<SearchScope> results = new ArrayList<SearchScope>();
		Feed searchFeed = null;

		ExtensibleElement feed = getFeed(scopesPath);
		if(feed.getAttributeValue(StringConstants.API_ERROR) == null){
			searchFeed = (Feed)getFeed(scopesPath);
		}else{
			return null;
		}

		if (searchFeed != null) {
			for(Entry entry:  searchFeed.getEntries()) {
				SearchScope searchScope = new SearchScope(entry);
				if (scopeId == null || searchScope.getId().equalsIgnoreCase(scopeId)) {
					results.add(searchScope);
				}
			}
		}
		return results;
	}
	
}

