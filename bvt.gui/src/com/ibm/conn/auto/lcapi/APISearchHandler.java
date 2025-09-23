package com.ibm.conn.auto.lcapi;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.lcapi.common.APIHandler;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.service.SearchService;

public class APISearchHandler extends APIHandler<SearchService> {
	
	private static final Logger log = LoggerFactory.getLogger(APISearchHandler.class);
	
	public APISearchHandler(String serverURL, String username, String password) {

		super("search", serverURL, username, password);
	}
	
	@Override
	protected SearchService getService(AbderaClient abderaClient, ServiceEntry generalService) {
		return new SearchService(abderaClient, generalService);
	}
	
	public void updateComponentIndexNow(String component) throws UnsupportedEncodingException {
		log.info("INFO: Force Update Index");
		service.ResetIndexing(component);
	}
	
	public boolean waitForIndexer(String component, String searchFor, int waitMinutes) throws Exception {
		Feed result = null;
		boolean entryFound = false;
	
		Calendar endTime = Calendar.getInstance();
		endTime.add(Calendar.MINUTE, waitMinutes);
		while(Calendar.getInstance().before(endTime)) {
			result = (Feed) service.doBasicSearch(component, searchFor);
			if(result.getEntries().size() > 0) {
				entryFound = true;
				break;
			}
			Thread.sleep(10000);
		}
		
		return entryFound;
	}

	

}
