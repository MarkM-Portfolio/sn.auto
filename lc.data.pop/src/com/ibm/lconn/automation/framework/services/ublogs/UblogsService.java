package com.ibm.lconn.automation.framework.services.ublogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.AccessControlEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.ActivitystreamsEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.ApplicationEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.ConfigEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.JsonEntries;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.JsonEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.UblogLikes;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.UblogObject;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.UblogReplies;



/**
 * Profiles Service object handles getting/posting data to the Connections Profiles service.
 * 
 * @author Ping wang - wangpin@us.ibm.com
 *  		Modified from ProfilesService
 */
public class UblogsService  extends LCService {

	private HashMap<String, String> ublogIds = new HashMap<String, String>();
	
	public UblogsService(AbderaClient client, 
			ServiceEntry service) {
		this(client, service, new HashMap<String, String>());
	}
	
	public UblogsService(AbderaClient client, 
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
		try {

			String JSON = getResponseString(uri);
			
			if (JSON != null){
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
			}else{
				return null;
			}			
		} catch (Exception e) {
			getApiLogger().error(e.getMessage());
			getApiLogger().error(e.getStackTrace().toString());
			//e.printStackTrace();
		}
		
		return apps;
	}
	
	public ArrayList<ActivitystreamsEntry> getActivitystreamsEntries ( String uri ) {
		//GET to /activitystreams/@me/@all ...etc...
		ArrayList<ActivitystreamsEntry> asEntries = new ArrayList<ActivitystreamsEntry>();
		try {
			String JSON = getResponseString(uri);
			if (JSON != null){
				JsonEntries je = new JsonEntries(JSON);
				je.getTotalResults();
				JSONArray jsonEntryArray = je.getJsonEntryArray();			
				
				@SuppressWarnings("unchecked")
				Iterator <OrderedJSONObject> it1 = jsonEntryArray.iterator();
				while (it1.hasNext()) { // for each entry
					OrderedJSONObject obj1 = (OrderedJSONObject) it1.next();
					ActivitystreamsEntry as = new ActivitystreamsEntry(obj1);
					asEntries.add(as);					
				}
			} else{
				return null;
			}

		} catch (Exception e) {
			getApiLogger().error(e.getMessage());
			getApiLogger().error(e.getStackTrace().toString());
		}
		
		return asEntries;
	}
	
	public UblogLikes getLikesEntry ( UblogObject obj ) {
		//GET UblogLikes
		UblogLikes likes = new UblogLikes(obj.getLikes());
			
		return likes;
	}

	public ArrayList<UblogObject> getAllUblog ( String uri ) {
		//GET to /ublog/{userid|communityid}/@all 
		ArrayList<UblogObject> ublogs = new ArrayList<UblogObject>();
		try {
			String JSON = getResponseString(uri);
			
			if (JSON != null){
				JsonEntries ub = new JsonEntries(JSON);
				ub.getTotalResults();
				JSONArray jsonEntryArray = ub.getJsonEntryArray();			
				
				@SuppressWarnings("unchecked")
				Iterator <OrderedJSONObject> it1 = jsonEntryArray.iterator();
				while (it1.hasNext()) { // for each entry
					OrderedJSONObject obj1 = (OrderedJSONObject) it1.next();
					UblogObject uobj = new UblogObject(obj1);
					ublogs.add(uobj);
					
				}
			} else{
				return null;
			}
		} catch (Exception e) {
			getApiLogger().error(e.getMessage());
			getApiLogger().error(e.getStackTrace().toString());
		}
		
		return ublogs;
	}


	public void printUblogObject(UblogObject ublog) {
		//--print ublog object info
		System.out.println("** start display ublog and its comments, recommendations **");
		System.out.println(ublog.getSummary());
		System.out.println(ublog.getId());
		//System.out.println(ublog.getObjectType());
		System.out.println("---------------");
		ublogIds.put(ublog.getId(), ublog.getSummary());
		
		if (ublog.getObjectType().contains("note")){
			OrderedJSONObject reply_obj = ublog.getReplies();					
			UblogReplies replieso = new UblogReplies(reply_obj);
			replieso.getTotalItems();
			replieso.getUrl();				
			JSONArray jsonItemsArray = replieso.getJsonItemsArray();
			
			if (jsonItemsArray != null){
				@SuppressWarnings("unchecked")
				Iterator <OrderedJSONObject> it2 = jsonItemsArray.iterator();
				while (it2.hasNext()) { // for each items
					OrderedJSONObject obj4 = (OrderedJSONObject) it2.next();
					
					UblogObject replieObj = new UblogObject(obj4);
					replieObj.getAuthor();
					System.out.println(replieObj.getSummary());
					System.out.println(replieObj.getId());							
					//System.out.println(replieObj.getObjectType());
					System.out.println(replieObj.getPublished());
				}
			}
			
			OrderedJSONObject like_obj = ublog.getLikes();					
			UblogReplies likeso = new UblogReplies(like_obj);
			JSONArray jsonItems = likeso.getJsonItemsArray();
			if (jsonItems != null){
				@SuppressWarnings("unchecked")
				Iterator <OrderedJSONObject> it2 = jsonItems.iterator();
				while (it2.hasNext()) { // for each items
					OrderedJSONObject obj4 = (OrderedJSONObject) it2.next();
					
					UblogObject replieObj = new UblogObject(obj4);
					replieObj.getAuthor();
												
					System.out.println(replieObj.getObjectType());
					System.out.println(replieObj.getId());
					System.out.println(replieObj.getPublished());
					System.out.println("*********************");
				}
			}
		}
	}
	
	public boolean deleteAllUblog ( String uri ) {
		//DELETE to /ublog/{userid|communityid}/@all/{entryid}
		boolean result = false;
		ArrayList<UblogObject> ublogs = new ArrayList<UblogObject>();
		try {
			ublogs = getAllUblog(uri);
			if (ublogs != null){
				for (UblogObject ublog : ublogs){

					String delete_uri = uri+"/"+ublog.getId();
					//int i = deleteWithResponseStatus(delete_uri);
					getApiLogger().debug("Delete Status: "+deleteWithResponseStatus(delete_uri));
				}
			}
			
			result = true;
		} catch (Exception e) {
			getApiLogger().error(e.getMessage());
			getApiLogger().error(e.getStackTrace().toString());
		}
		
		return result;
	}
	
	public UblogObject getEntry ( String uri ) {
		//GET a given entry   /ublog/{userid|communityid}/@all/{entryid}

		String JSON = getResponseString(uri);
		if (JSON != null){
			JsonEntry ub = new JsonEntry(JSON);
			OrderedJSONObject jsonE = ub.getJsonEntry();
			UblogObject ublog = new UblogObject(jsonE);			
			//printUblogObject(ublog);
			
			return ublog;
		}

		return null;
	}
	
	public ConfigEntry getConfigEntry ( String uri ) {
		//GET a given entry   /ublog/@config/settings

		String JSON = getResponseString(uri);
		if (JSON != null){
			JsonEntry je = new JsonEntry(JSON);
			OrderedJSONObject jsonE = je.getJsonEntry();
			ConfigEntry ce = new ConfigEntry(jsonE);
			ce.getMicroblogEntryMaxChars();
						
			return ce;							
		}
		
		return null;
	}
	
	
	public String createEntry ( String uri, String entrySt ) {
		//POST   ublog, comment, recommendation etc.
		String entry_id = null;
		String JSON = postResponseJSONString(uri,entrySt);
			
		if (JSON != null && getRespStatus() != 403){
			JsonEntry ub = new JsonEntry(JSON);//result.getResponseBody());
			OrderedJSONObject jsonEntry = ub.getJsonEntry();
			UblogObject ublog = new UblogObject(jsonEntry);
			entry_id = ublog.getId();
		}

		return entry_id;
	}
	
	
	/*
	 * Method to post microblog entry to the server and to get HTTP post response code, return response code
	 * to post @Mentions status update
	 */
	public String createMentionsEntry ( String uri, String entrySt ) {
		//POST   ublog, @mention etc.
		return postResponseJSONString(uri, entrySt);
	}

	
	public ExtensibleElement getAtomEntry ( String uri) {
		return getFeed(uri);
	}
	
	public String createUblogEntry ( String uri, String entrySt ) {
		//POST   ublog, comment, recommendation etc.
		String entry_id = null;

		String JSON = postResponseJSONString( uri,  entrySt);
			
		if (getRespStatus() == 200){
			JsonEntry ub = new JsonEntry(JSON);
			OrderedJSONObject jsonEntry = ub.getJsonEntry();
			UblogObject ublog = new UblogObject(jsonEntry);
			entry_id = ublog.getId();
		}

		return entry_id;
	}
		
	
	public AccessControlEntry getAccessControlEntry ( String uri ) {
		//GET to /news/microblogging/settings/cid.action ...
		AccessControlEntry as = null;
		
		String JSON = getResponseString(uri);
		if (JSON != null){
			try {
				OrderedJSONObject obj = new OrderedJSONObject(JSON);
				as = new AccessControlEntry(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
					
		return as;
	}
	
	public AccessControlEntry editAccessControlEntry(String uri, String entry){
		//UPDATE to /news/microblogging/settings/cid.action ...
		AccessControlEntry as = null;

		String JSON = putResponseString(uri, entry );
		if (JSON != null){
			try {
				OrderedJSONObject obj = new OrderedJSONObject(JSON);
				as = new AccessControlEntry(obj);		
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
					
		return as;
	}
	
	public void deleteASEntry ( String uri ) throws Exception{
		getApiLogger().debug("Delete Status: "+deleteWithResponseStatus(uri));

	}

}