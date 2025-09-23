package com.ibm.lconn.automation.framework.services.opensocial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.ActivitystreamsEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.ApplicationEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.ConfigEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.EventEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.JsonEntries;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.JsonEntry;

/**
 * Profiles Service object handles getting/posting data to the Connections Profiles service.
 * 
 * @author Ping wang - wangpin@us.ibm.com
 *  		Modified from ProfilesService
 */
public class ActivitystreamsService  extends LCService {
	
	public ActivitystreamsService(AbderaClient client, 
			ServiceEntry service) {
		super(client, service);
	}
	
	public ActivitystreamsService(AbderaClient client, 
			ServiceEntry service, Map<String, String> headers) {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}
	}
	
	public ExtensibleElement getATOMFeed( String uri) {
		return getFeed(uri);
	}
	
	
	public ArrayList<ApplicationEntry> getASApplications ( String uri ) {
		//GET to /connections/opensocial/rest/activitystreams/@me/@applications 
		ArrayList<ApplicationEntry> apps = new ArrayList<ApplicationEntry>();

		String JSON = getResponseString	(uri);	
		
		if (JSON == null){
			return null;
		}
		
		JsonEntries ub = new JsonEntries(JSON);
		ub.getTotalResults();
		JSONArray jsonEntryArray = ub.getJsonEntryArray();			
		
		@SuppressWarnings("unchecked")
		Iterator <OrderedJSONObject> it1 = jsonEntryArray.iterator();
		while (it1.hasNext()) { // for each entry
			OrderedJSONObject obj1 = (OrderedJSONObject) it1.next();
			ApplicationEntry app = new ApplicationEntry(obj1);
			
			apps.add(app);				
		}
			
		return apps;
	}
	
	public ArrayList<ActivitystreamsEntry> getActivitystreamsEntries ( String uri ) {
		//GET to /activitystreams/@me/@all ...etc...
		ArrayList<ActivitystreamsEntry> asEntries = new ArrayList<ActivitystreamsEntry>();

		String JSON = getResponseString	(uri);		
		if (JSON == null || getRespStatus()!= 200){
			return null;
		}

		JsonEntries je = new JsonEntries(JSON);
		je.getTotalResults();
		JSONArray jsonEntryArray = je.getJsonEntryArray();	
		
		if (jsonEntryArray==null){
			// empty entry
			return asEntries;
		}
		
		@SuppressWarnings("unchecked")
		Iterator <OrderedJSONObject> it1 = jsonEntryArray.iterator();
		while (it1.hasNext()) { // for each entry
			OrderedJSONObject obj1 = (OrderedJSONObject) it1.next();
			ActivitystreamsEntry as = new ActivitystreamsEntry(obj1);
			asEntries.add(as);			
		}
					
		return asEntries;
	}
	
	
	public ActivitystreamsEntry getActivitystreamsEntry ( String uri ) {
		//GET to /activitystreams/@me/@all/@all/EventId ...etc...
		ActivitystreamsEntry as = null;

		String JSON = getResponseString	(uri);	
					
		if (JSON == null || getRespStatus() != 200){
			return null;
		}
		System.out.println(" ------:------ AS entry : "+JSON);
		JsonEntry je = new JsonEntry(JSON);
		
		OrderedJSONObject obj1 = (OrderedJSONObject) je.getJsonEntry();
		as = new ActivitystreamsEntry(obj1);			

		return as;
	}
	
	public ActivitystreamsEntry getActivitystreamsWebEntry ( String uri ) {
		//GET to /activitystreams/@me/@all/@all/EventId ...etc...
		ActivitystreamsEntry as = null;

		String JSON = getResponseString(uri);
		if (JSON == null){
			return null;
		}
		
		JsonEntry je = new JsonEntry(JSON);
		
		OrderedJSONObject obj1 = (OrderedJSONObject) je.getJsonEntry();
		as = new ActivitystreamsEntry(obj1);			
				
		return as;
	}

	public ConfigEntry getConfigEntry ( String uri ) {
		//GET a given entry   /ublog/@config/settings

		String JSON = getResponseString	(uri);	
		
		if (JSON == null){
			return null;
		}
		
		JsonEntry je = new JsonEntry(JSON);
		OrderedJSONObject jsonE = je.getJsonEntry();
		ConfigEntry ce = new ConfigEntry(jsonE);
		ce.getMicroblogEntryMaxChars();
					
		return ce;
						
	}
	
	public EventEntry getEventEntry ( String uri ) {
		//GET a given entry   

		String JSON = getResponseString	(uri);	
		
		if (JSON == null || getRespStatus() != 200){
			return null;
		}
		
		JsonEntry je = new JsonEntry(JSON);
		OrderedJSONObject jsonE = je.getJsonEntry();
		EventEntry ee = new EventEntry(jsonE);
		//String id = ee.getId();
					
		return ee;
						
	}
	
	public String createASEntry ( String uri, String entrySt ) {
		//POST   ActivityStreams.
		return postResponseJSONString(uri, entrySt);
	}
	
	public int deleteASEntry(String url)
	{
		ClientResponse response = deleteWithResponse(url);
		
		return getRespStatus();
	}
	
	public String validateOpensocialPath(ActivitystreamsEntry as) {
		String result = null;
		OrderedJSONObject osObject = as.getOpenSocial();
		if ( osObject != null){
			@SuppressWarnings("unchecked")
			Set<String> set1 = osObject.keySet();
			Iterator<String> it1 = set1.iterator();
			while (it1.hasNext()) {
				String key1 = it1.next().toString();
				if (key1.contains("embed")) {
					OrderedJSONObject obj1;
					try {
						obj1 = (OrderedJSONObject) osObject.getJSONObject(key1);
					
						@SuppressWarnings("unchecked")
						Set<String> set2 = obj1.keySet();
						Iterator<String> it2 = set2.iterator();
						while (it2.hasNext()) {
							String key2 = it2.next().toString();
							if (key2.contains("context")) {
								OrderedJSONObject obj2 = (OrderedJSONObject) obj1.getJSONObject(key2);
	
								@SuppressWarnings("unchecked")
								Set<String> set3 = obj2.keySet();
								Iterator<String> it3 = set3.iterator();
								while (it3.hasNext()) {
									String key3 = it3.next().toString();
									if (key3.contains("connectionsContentUrl")) {
										result = obj2.getString(key3);
										//result = value.contains("connections/opensocial");
										break;
									}
								}
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} 
		}
		return result;
	}
	

}