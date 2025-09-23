package com.ibm.lconn.automation.framework.services.catalog;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 * Catalog Service object handles getting/posting data to the Communities Catalog service.
 * 
 * @author Einat Avikser
 */
public class CatalogService extends LCService {

	public final static String CATALOG_VIEW_LIST_NAME="LIST";

	public static final String CATALOG_VIEW_PATH_POSTFIX_OWN="/communities/service/html/ownedcommunities";
	public static final String CATALOG_VIEW_KEY_OWN="own";

	public static final String CATALOG_VIEW_PATH_POSTFIX_MY="/communities/service/html/mycommunities";
	public static final String CATALOG_VIEW_KEY_MY="member";
	
	public static final String CATALOG_VIEW_PATH_POSTFIX_FOLLOW="/communities/service/html/followedcommunities";
	public static final String CATALOG_VIEW_KEY_FOLLOW="follow";
	
	public static final String CATALOG_VIEW_PATH_POSTFIX_INVITE="/communities/service/html/communityinvites";
	public static final String CATALOG_VIEW_KEY_INVITE="invite";
	
	public static final String CATALOG_VIEW_PATH_POSTFIX_PUBLIC="/communities/service/html/allcommunities";
	public static final String CATALOG_VIEW_KEY_PUBLIC="public";
	
	public static final String CATALOG_VIEW_PATH_POSTFIX_TRASH="/communities/service/html/trashedcommunities";
	public static final String CATALOG_VIEW_KEY_TRASH="trash";
	
	public static final String CATALOG_VIEW_PATH_POSTFIX_CREATE="/communities/service/html/createdcommunities";
	public static final String CATALOG_VIEW_KEY_CREATE="created";

	public static final String CATALOG_VIEW_PATH_POSTFIX_ALLMY="communities/service/html/allmycommunities";
	public static final String CATALOG_VIEW_KEY_ALLMY="allmy";
	 
	private static Abdera abdera = new Abdera();
	private static AbderaClient catlogAdminClient= new AbderaClient(abdera);
	
	public CatalogService(AbderaClient client) {
		this(client, new HashMap<String, String>());
	}
	
	public CatalogService(AbderaClient client, Map<String, String> headers) {
		super(client, null);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}	
		if(service != null)
			this.setFoundService(true);
	}
	
	public ArrayList<Community> getCommunitiesCatalogPublicCommunities(CatalogListRequest catalogListRequest) {
		return catalog(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_PUBLIC,
				catalogListRequest);
	}

	public ArrayList<Community> getCommunitiesCatalogMyCommunities(CatalogListRequest catalogListRequest) {
		return catalog(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_MY,
				catalogListRequest);
	}
	
	public ArrayList<Community> getCommunitiesCatalogAllMyCommunities(CatalogListRequest catalogListRequest) {
		return catalog(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_ALLMY,
				catalogListRequest);
	}
	
	public ArrayList<Community> communitiesCatalogSearchCommunities(CatalogListRequest catalogListRequest) {
		return catalog(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_SEARCH,
				catalogListRequest);
	}
	
	public ArrayList<Community> getCommunitiesCatalogFollowingCommunities(CatalogListRequest catalogListRequest) {
		return catalog(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_FOLLOWING,
				catalogListRequest);
	}
	
	public ArrayList<Community> getCommunitiesCatalogInvitedCommunities(CatalogListRequest catalogListRequest) {
		return catalog(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_INVITED,
				catalogListRequest);
	}
	
	public ArrayList<Community> getCommunitiesCatalogOwnedCommunities(CatalogListRequest catalogListRequest) {
		return catalog(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_OWNED,
				catalogListRequest);
	}
	
	public ArrayList<Community> getCommunitiesCatalogRestrictedCommunities(CatalogListRequest catalogListRequest) {
		return catalog(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_RESTRICTED,
				catalogListRequest);
	}
	
	public ArrayList<Community> getCommunitiesCatalogTrashedCommunities(CatalogListRequest catalogListRequest) {
		return catalog(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_TRASHED,
				catalogListRequest);
	}
	
	public ArrayList<Tag> getCommunitiesCatalogTags(CatalogTagRequest catalogTagRequest) {
		return getCatalogTags(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_TAGS,
				catalogTagRequest);
	}
	
	public ArrayList<Tag> getCommunitiesCatalogPublicTags(CatalogTagRequest catalogTagRequest) {
		return getCatalogTags(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_PUBLIC_TAGS,
				catalogTagRequest);
	}
	
	public ArrayList<Tag> getCommunitiesCatalogMyTags(CatalogTagRequest catalogTagRequest) {
		return getCatalogTags(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_MY_TAGS,
				catalogTagRequest);
	}
	
	public ArrayList<String> communitiesCatalogTagsCompletion(CatalogTypeaheadRequest catalogTypeaheadRequest) {
		return getCatalogTypeaheadFeed(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_TAGS_COMPLETION,
				catalogTypeaheadRequest);
	}
	
	public ExtensibleElement getCommunitiesCatalogAdmin() {
		loginToCatalogAdminService();
		return getFeed(catlogAdminClient,URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_ADMIN_GET 
				+ "?" + URLConstants.CATALOG_COLLECTION_ID_PARAM);
	}
	
	
	public ArrayList<Community> getCommunitiesCatalogCreatedCommunities(CatalogListRequest catalogListRequest) {
		return catalog(URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_CREATED,
				catalogListRequest);
	}
	
	
	public void indexNow() {
//		loginToCatalogAdminService();
//		Factory factory = abdera.getFactory();
//		Entry entry = factory.newEntry();
//		return postFeed(catlogAdminClient,URLConstants.SERVER_URL + URLConstants.BASE_CATALOG_URL + URLConstants.CATALOG_ADMIN_START  
//				+ "?" + URLConstants.CATALOG_COLLECTION_ID_PARAM + "&" + URLConstants.CATALOG_CRAWLER_ID_PARAM, entry);
		try {
			getApiLogger().debug("Sleeping for 60 seconds to allow catalog indexing");
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loginToCatalogAdminService() {
		loginToCatalogAdminService(StringConstants.ADMIN_USER_NAME, StringConstants.ADMIN_USER_PASSWORD);
	}
	
	private void loginToCatalogAdminService(String username, String password) {
		getApiLogger().debug("loginToCatalogAdminService start");
		// login with admin user
		HttpClient http = new HttpClient();
		http.getState().clearCookies();
		
		Cookie[] cookies;
	
		if ( StringConstants.AUTHENTICATION.equalsIgnoreCase(StringConstants.Authentication.BASIC.toString()) ) {
			cookies = executeJLogin(http, username, password);
		} 
		else {
			cookies = executeLogin(http, username, password);
		}
		for (Cookie cookie : cookies) {
			catlogAdminClient.addCookies(cookie);
		}
		getApiLogger().debug("loginToSearchAdminService end");
	}
	
	
	public ArrayList<Community> catalogTypeahead(String sourceURL, CatalogTypeaheadRequest catalogTypeaheadRequest){
		
		String catalogTypeaheadPath = buildCatalogTypeaheadPath(sourceURL, catalogTypeaheadRequest);

		getApiLogger().debug("catalogTypeaheadPath: " + catalogTypeaheadPath);

		ArrayList<Community> results = calculateCatalogResults(catalogTypeaheadPath);
		
		return results;
	}
	
	public ArrayList<Community> catalog(String sourceURL, CatalogListRequest catalogListRequest){
		
		String catalogPath = buildCatalogPath(sourceURL, catalogListRequest);

		getApiLogger().debug("catalogPath: " + catalogPath);

		ArrayList<Community> results = calculateCatalogResults(catalogPath);
		
		return results;
	}

	private ArrayList<Tag> getCatalogTags(String sourceURL, CatalogTagRequest catalogTagRequest) {
		
		String catalogTagsPath = buildCatalogTagsPath(sourceURL, catalogTagRequest);

		getApiLogger().debug("catalogTagsPath: " + catalogTagsPath);

		ArrayList<Tag> results = calculateCatalogTagResults(catalogTagsPath);

		return results;
	}


	private ArrayList<Tag> calculateCatalogTagResults(String catalogTagsPath) {
		ArrayList<Tag> results = new ArrayList<Tag>();
		
		ClientResponse response = client.get(catalogTagsPath, this.options);

		int status = response.getStatus();
		if (status != 200){
			getApiLogger().debug("Fail to calculateCatalogTagResults - status : " + status);
			fail("Fail to excecute - status : " + status);
			return null;
		}
		String responseStr;
		try {
			responseStr = readResponse(response.getReader());
			getApiLogger().debug(responseStr);
			JSONArray tagsArray = new JSONArray(responseStr);
			for(int i=0; i<tagsArray.length(); i++) {
				String label = ((JSONObject)tagsArray.get(i)).getString("label");
				int weight = Integer.parseInt(((JSONObject)tagsArray.get(i)).getString("weight"));
				Tag tag = new Tag(label,weight);
				results.add(tag);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return results;
	}

	private ArrayList<Community> calculateCatalogResults(String catalogPath){
		
		ArrayList<Community> results = new ArrayList<Community>();
		Feed catalogFeed = null;
		
		ExtensibleElement feed = getFeed(catalogPath);
		if(feed.getAttributeValue(StringConstants.API_ERROR) == null){
			catalogFeed = (Feed)getFeed(catalogPath);
		}else{
			return null;
		}

		if (catalogFeed != null) {
			getApiLogger().debug("catalogFeed number of entries: " + catalogFeed.getEntries().size());
			for(Entry entry : catalogFeed.getEntries()) {
				Community result = new Community(entry);
				results.add(result);
			}
		}else{
			getApiLogger().debug("catalogFeed is null");
		}
		
		return results;
	}
	
	private ArrayList<String> getCatalogTypeaheadFeed(String url, CatalogTypeaheadRequest catalogTypeaheadRequest){
		String catalogPath = buildCatalogTypeaheadPath(url, catalogTypeaheadRequest);

		ArrayList<String> completions = calculateCatalogTypeaheadResults(catalogPath.toString());
		
		return completions;
	}
	
	private ArrayList<String> calculateCatalogTypeaheadResults(String catalogTypeaheadPath) {
		ArrayList<String> results = new ArrayList<String>();
		ClientResponse response = client.get(catalogTypeaheadPath, this.options);
		getApiLogger().debug("calculateCatalogTagResults - path : " + catalogTypeaheadPath);

		int status = response.getStatus();
		if (status != 200){
			getApiLogger().debug("Fail to calculateCatalogTagResults - status : " + status);
			fail("Fail to excecute - status : " + status);
			return null;
		}
		String responseStr;
		try {
			responseStr = readResponse(response.getReader());
			getApiLogger().debug(responseStr);
			if (responseStr.startsWith("{}&&")){
				responseStr = responseStr.substring(4);
			}
			JSONArray completions = new JSONArray(responseStr);
			for(int i=0; i<completions.length(); i++) {
				String completion = (String)completions.get(i);
				results.add(completion);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return results;
	}

	private String buildCatalogTypeaheadPath(String url, CatalogTypeaheadRequest catalogTypeaheadRequest){
		StringBuffer catalogPath = new StringBuffer();
		catalogPath.append(url);
		
		if(catalogPath.toString().lastIndexOf("?") == -1){
			catalogPath.append("?");
		}else{
			catalogPath.append("&");
		}
		addPrefix(catalogPath ,catalogTypeaheadRequest.getPrefix());
		return catalogPath.toString();
	}
	
	private void addPrefix(StringBuffer catalogPath ,String prefix){
		if(prefix != null && prefix.length() != 0){
			catalogPath.append("prefix=");
			catalogPath.append(prefix);
			catalogPath.append("&");
		}
		catalogPath.deleteCharAt(catalogPath.length() - 1);
	}
	
	private String buildCatalogPath(String url, CatalogListRequest catalogListRequest){
		return CatalogListRequest.buildPath(url, catalogListRequest);
	}
	
	private String buildCatalogTagsPath(String url, CatalogTagRequest catalogTagRequest){
		return CatalogTagRequest.buildPath(url, catalogTagRequest);
	}
	
	public ArrayList<CatalogView> getCommunitiesCatalogViews(CatalogViewRequest catalogViewRequest) {
		String catalogViewPath = URLConstants.SERVER_URL + URLConstants.CATALOG_VIEWS_URL;
		catalogViewPath = CatalogViewRequest.buildPath(catalogViewPath, catalogViewRequest);
		getApiLogger().debug("catalogViewPath: " + catalogViewPath);
		ArrayList<CatalogView> results = calculateCatalogViewResults(catalogViewPath);
		return results;
	}

	private ArrayList<CatalogView> calculateCatalogViewResults(String catalogViewPath) {
		ArrayList<CatalogView> results = new ArrayList<CatalogView>();
		
		ClientResponse response = client.get(catalogViewPath, this.options);

		int status = response.getStatus();
		if (status != 200){
			getApiLogger().debug("Fail to calculateCatalogViewResults - status : " + status);
			fail("Fail to excecute - status : " + status);
			return null;
		}
		String responseStr;
		try {
			responseStr = readResponse(response.getReader());
			getApiLogger().debug(responseStr);
			JSONObject catalogViews = new JSONObject(responseStr);
			JSONArray catalogViewArray = catalogViews.getJSONArray(CATALOG_VIEW_LIST_NAME);
			for(int i=0; i<catalogViewArray.length(); i++) {
				JSONObject currentView = (JSONObject)catalogViewArray.get(i);
				String key = currentView.getString(CatalogView.CATALOG_VIEW_KEY);
				String title = currentView.getString(CatalogView.CATALOG_VIEW_TITLE);
				String description = currentView.getString(CatalogView.CATALOG_VIEW_DESCRIPTION);
				String path = currentView.getString(CatalogView.CATALOG_VIEW_PATH);
				int order = currentView.getInt(CatalogView.CATALOG_VIEW_ORDER);
				CatalogView catalogView = new CatalogView(key,title,description,path, order);
				results.add(catalogView);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		return results;
	}

	
}

