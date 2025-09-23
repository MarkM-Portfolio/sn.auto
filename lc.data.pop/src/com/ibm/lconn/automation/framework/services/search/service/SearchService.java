package com.ibm.lconn.automation.framework.services.search.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.wink.json4j.OrderedJSONObject;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.search.data.CategoryConstraint;
import com.ibm.lconn.automation.framework.services.search.data.ContextPath;
import com.ibm.lconn.automation.framework.services.search.data.Facet;
import com.ibm.lconn.automation.framework.services.search.data.FieldConstraint;
import com.ibm.lconn.automation.framework.services.search.data.RangeConstraint;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SocialConstraint;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.data.SortOrder;
import com.ibm.lconn.automation.framework.services.search.nodes.SearchResult;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;

/**
 * Activities Service object handles getting/posting data to the Connections Activities service.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class SearchService extends LCService {
	
	protected static final String IBM_XML_NAMESPACE = "http://www.ibm.com/search/content/2010";
	private static final String CLASS_NAME = SearchService.class.getName();
	protected static final Logger logger = Logger.getLogger(CLASS_NAME);

	public static final String SCHEME_TYPE = "http://www.ibm.com/xmlns/prod/sn/type";
	public static final String SCHEME_ACCESS = "http://www.ibm.com/xmlns/prod/sn/accesscontrolled";
	public static final String SCHEME_DOC_TYPE = "http://www.ibm.com/xmlns/prod/sn/doctype";
	public static final String SCHEME_COMPONENT = "http://www.ibm.com/xmlns/prod/sn/component";


	private static final String UTF_8_ENCODING_TYPE = "UTF-8";
	
	private static final String COMPONENT = "component";
	private static final String COMPONENTS = "components";
	private static final String EMAIL = "email";
	private static final String PAGE = "page";
	private static final String QUERY_LANG = "queryLang";
	private static final String LANG = "lang";
	private static final String PAGE_SIZE = "pageSize";
	private static final String QUERY_TEXT = "query";
	private static final String TAG = "tag";
	private static final String USER_ID = "userid";
	private static final String START = "start";
	private static final String EVIDENCE = "evidence";
	private static final String LOCALE = "locale";
	private static final String SORT_KEY = "sortKey";
	private static final String SCOPE = "scope";
	private static final String SORT_ORDER = "sortOrder";
	private static final String SEARCH_REQUEST_ENGINE = "searchRequestEngine";
	
	
	/**
	 * Constructor to create a new Search Service helper object. 
	 * 
	 * This object contains helper methods for all API calls that are supported by the Search service.
	 * 
	 * @param client	the authenticated AbderaClient that is used to handle requests to/from server
	 * @param service	the ServiceEntry that contains information about the Search service from the server ServiceConfigs file
	 */
	public SearchService(AbderaClient client, ServiceEntry service) {
		super(client, service);
		
		if(service != null)
			this.setFoundService(true);
	}
	

	public String ResetIndexing(String component) throws UnsupportedEncodingException{
		

		getApiLogger().debug(SearchService.class.getName());
		String uri = service.getServiceURLString()+ "/searchAdmin?command=indexNow&params=" + 
		URLEncoder.encode("[\"" + component + "\"]", UTF_8_ENCODING_TYPE);

		try {
			// default user
			//HttpResponse response = doHttpGet( uri );
			//String JSON = response.getResponseBody();
			String JSON = getResponseString(uri);
			if (JSON != null){
				OrderedJSONObject obj0 = new OrderedJSONObject(JSON);
				@SuppressWarnings("unchecked")
				Set<String> set0 = obj0.keySet();
				Iterator<String> it0 = set0.iterator();
				while (it0.hasNext()) {
					String key0 = it0.next().toString();
					if (key0.contains("success")){
						getApiLogger().debug("success : "+obj0.getString(key0));
						return  obj0.getString(key0);
					}
				}
			}
			
			
		} catch (Exception e) {
			getApiLogger().error(e.getMessage());
			getApiLogger().error(e.getStackTrace().toString());
		}
		return null;

	}	
	
	public String ResetIndexing() throws UnsupportedEncodingException{
		String ALL_COMPONENTS="activities,blogs,communities,dogear,files,forums,profiles,wikis,status_updates";  // ,ecm_files,people_finder";
		return ResetIndexing(ALL_COMPONENTS);
		
	}

	public SearchResponse searchAllPublic(SearchRequest searchRequest) {
		return search(service.getServiceURLString() + searchRequest.getContextPathString() + URLConstants.SEARCH_PUBLIC, searchRequest);
	}
	
	public SearchResponse searchAllPublicPrivate(SearchRequest searchRequest) {
		return search(service.getServiceURLString() + searchRequest.getContextPathString() + URLConstants.SEARCH_PUBLIC_PRIVATE, searchRequest);
	}
	
	public SearchResponse searchAllPublicFacetPeople(SearchRequest searchRequest) {
		return search(service.getServiceURLString() + searchRequest.getContextPathString() + URLConstants.SEARCH_PEOPLE_PUBLIC, searchRequest);
	}
	
	public SearchResponse searchAllPublicPrivateFacetPeople(SearchRequest searchRequest) {
		return search(service.getServiceURLString() + searchRequest.getContextPathString() + URLConstants.SEARCH_PEOPLE_PUBLIC_PRIVATE, searchRequest);
	}
	
	public SearchResponse searchAllPublicFacetTags(SearchRequest searchRequest) {
		return search(service.getServiceURLString() + searchRequest.getContextPathString() + URLConstants.SEARCH_TAGS_PUBLIC, searchRequest);
	}
	
	public SearchResponse searchAllPublicPrivateFacetTags(SearchRequest searchRequest) {
		return search(service.getServiceURLString() + searchRequest.getContextPathString() + URLConstants.SEARCH_TAGS_PUBLIC_PRIVATE, searchRequest);
	}
	
	public SearchResponse searchAllPublicFacetDate(SearchRequest searchRequest) {
		return search(service.getServiceURLString() + searchRequest.getContextPathString() + URLConstants.SEARCH_DATE_PUBLIC, searchRequest);
	}
	
	public SearchResponse searchAllPublicPrivateFacetDate(SearchRequest searchRequest) {
		return search(service.getServiceURLString() + searchRequest.getContextPathString() + URLConstants.SEARCH_DATE_PUBLIC_PRIVATE, searchRequest);
	}

	public ExtensibleElement searchForTags(String component, String query){
		return searchForTags(ContextPath.atom, component, query);
	}
	
	public ExtensibleElement searchForTags(ContextPath contextPath, String component, String query){
		return getFeed(service.getServiceURLString() + "/" + contextPath + URLConstants.SEARCH_TAGS_PUBLIC + "?component=" + component + "&query=" + query);
	}
	
	public ExtensibleElement searchForFacetDate(String component, String query){
		return searchForFacetDate(ContextPath.atom, component, query);
	}
	
	public ExtensibleElement searchForFacetDate(ContextPath contextPath, String component, String query){
		return getFeed(service.getServiceURLString() +  "/" + contextPath + URLConstants.SEARCH_DATE_PUBLIC + "?component=" + component + "&query=" + query);
	}
	
	public ExtensibleElement doBasicSearch(String component, String query) throws Exception{
		return doBasicSearch(ContextPath.atom, component, query);
	}
	
	public ExtensibleElement doBasicSearch(ContextPath contextPath, String component, String query) throws Exception{
		return getFeed(service.getServiceURLString() +  "/" + contextPath + URLConstants.SEARCH_PUBLIC_PRIVATE + "?query=" + encodeURL(query) + "&scope=" + component);		
	}
	public ExtensibleElement doBasicGet(String url) throws Exception{
		return getFeed(url);
		
	}
	
	//tags typeahead public
	public SearchResponse searchTagsTypeaHeadPublic(String tag, String containerId) {
		return searchTagsTypeaHeadPublic(ContextPath.atom, tag, containerId);
	}
	
	public SearchResponse searchTagsTypeaHeadPublic(ContextPath contextPath, String tag, String containerId) {
		return search(service.getServiceURLString() +  "/" + contextPath + URLConstants.TAGS_TYPEAHEAD_PUBLIC, tag, containerId);
	}
	
	//tags typeahead public private
	public SearchResponse searchTagsTypeaHeadPublicPrivate(String tag, String containerId) {
		return searchTagsTypeaHeadPublicPrivate(ContextPath.atom, tag, containerId);
	}
	
	public SearchResponse searchTagsTypeaHeadPublicPrivate(ContextPath contextPath, String tag, String containerId) {
		return search(service.getServiceURLString() +  "/" + contextPath + URLConstants.TAGS_TYPEAHEAD_PRIVATE, tag, containerId);
	}
	
	public String encodeURL(String url) {
		String newURL = "";
		if(url.contains(" ")){
			newURL = url.replace(" ", "+");
			return newURL;
		}
		
		return url;
	}
	

	public ExtensibleElement getAllPublicFacetPeopleNew(String component, String components, String email, Lang lang, int page, int pageSize, String queryText, String tag, String userid) {
		return getAllPublicFacetPeopleNew(ContextPath.atom, component, components, email, lang, page, pageSize, queryText, tag, userid);
	}
	
	public ExtensibleElement getAllPublicFacetPeopleNew(ContextPath contextPath, String component, String components, String email, Lang lang, int page, int pageSize, String queryText, String tag, String userid) {
		return getFeed(searchPath(service.getServiceURLString() + "/" + contextPath + URLConstants.SEARCH_PEOPLE_PUBLIC, component, components, email, lang, page, pageSize, queryText, tag, userid));
	}
	
	public ExtensibleElement getAllPublicFacetActivities(String component, String components, String email, Lang lang, int page, int pageSize, String queryText, String tag, String userid){
		return getAllPublicFacetActivities(ContextPath.atom, component, components, email, lang, page, pageSize, queryText, tag, userid);
		
	}
	public ExtensibleElement getAllPublicFacetActivities(ContextPath contextPath, String component, String components, String email, Lang lang, int page, int pageSize, String queryText, String tag, String userid){
		return getFeed(searchPath(service.getServiceURLString() + "/" +  contextPath + URLConstants.SEARCH_SOURCE_PUBLIC, component, components, email, lang, page, pageSize, queryText, tag, userid));
	}
	
	public String searchPath(String sourceURL, String component, String components, String email, Lang lang, int page, int pageSize, String queryText, String tag, String userid) {
		String searchPath = sourceURL;
		
		if(searchPath.lastIndexOf("?") == -1 )
			searchPath += "?";
		else
			searchPath += "&";
		
		if(component != null && component.length() != 0)
			searchPath += "component=" + component + "&";
	
		if(components != null && components.length() != 0)
			searchPath += "components=" + components + "&";
		
		if(email != null && email.length() != 0)
			searchPath += "email=" + email + "&";
		
		if(lang != null)
			searchPath += "lang=" + lang.toString() + "&";
		
		if(page > 0)
			searchPath += "page=" + page + "&";
		
		if(pageSize > 0)
			searchPath += "ps=" + pageSize + "&";
		
		if(queryText != null && queryText.length() != 0)
			searchPath += "query=" + encodeURL(queryText) + "&";
		
		if(tag != null && tag.length() != 0)
			searchPath += "tag=" + tag + "&";
		
		if(userid != null && userid.length() != 0)
			searchPath += "userid=" + userid + "&";
		
		
		return searchPath;
	}
	
	public SearchResponse search(String sourceURL, String tag, String containerId){
		String searchPath = sourceURL;
		
		if(searchPath.lastIndexOf("?") == -1 )
			searchPath += "?";
		else
			searchPath += "&";
		
		if(tag != null && tag.length() != 0)
			searchPath += "tag=" + tag + "&";
		
		if(containerId != null && containerId.length() != 0)
			searchPath += "containerId=" + containerId + "&";
	
		
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		
		Feed searchFeed = null;
		
		ExtensibleElement feed = getFeed(searchPath);
		if(feed.getAttributeValue(StringConstants.API_ERROR) == null){			
			if (feed instanceof Feed){
				searchFeed = (Feed)feed;
			}
		}else{
			getApiLogger().debug(feed.getAttributeValue(StringConstants.API_ERROR));
			
		}
		
		if (searchFeed != null) {
			for(Entry entry:  searchFeed.getEntries()) {
				SearchResult result = new SearchResult(entry);
				results.add(result);
			}
		}
		return new SearchResponse(results, getRespType(), getRespStatus());
	}
	
	private SearchResponse search(String sourceURL, SearchRequest searchRequest){
		StringBuffer searchPath = new StringBuffer();
		searchPath.append(sourceURL);
		
		if(searchPath.toString().lastIndexOf("?") == -1){
			searchPath.append("?");
		}else{
			searchPath.append("&");
		}
		addQueryText(searchPath ,searchRequest.getQuery());
		addScope(searchPath ,searchRequest.getScope());
		addScope(searchPath ,searchRequest.getScope_secondary());
		addComponent(searchPath ,searchRequest.getComponent());
		addComponents(searchPath ,searchRequest.getComponents());
		addEmail(searchPath ,searchRequest.getEmail());
		addQueryLang(searchPath ,searchRequest.getQueryLang());		
		addPage(searchPath ,searchRequest.getPage());
		addPageSize(searchPath ,searchRequest.getPageSize());
		addTag(searchPath ,searchRequest.getTag());
		addSortKey(searchPath, searchRequest.getSortKey());
		addSortOrder(searchPath, searchRequest.getSortOrder());
		addUserId(searchPath ,searchRequest.getUserid());
		addLocale(searchPath ,searchRequest.getLocale());
		addStart(searchPath ,searchRequest.getStart());
		addEvidence(searchPath ,searchRequest.getEvidence());
		addFieldConstraints(searchPath ,searchRequest.getFieldConstraints());
		addCategoryConstraints(searchPath ,searchRequest.getCategoryConstraints());
		addRangeConstraints(searchPath ,searchRequest.getRangeConstraints());
		addFieldNotConstraints(searchPath ,searchRequest.getFieldNotConstraints());
		addCategoryNotConstraints(searchPath ,searchRequest.getCategoryNotConstraints());
		addRangeNotConstraints(searchPath ,searchRequest.getRangeNotConstraints());
		addFactes(searchPath ,searchRequest.getFacets());
		addSocialConstraints(searchPath ,searchRequest.getSocialConstraints());
		addHighlight(searchPath ,searchRequest.getHighlight());
		addSearchRequestEngine(searchPath, searchRequest.getSearchRequestEngine());
		
		searchPath.deleteCharAt(searchPath.length() - 1);
		
		SearchResponse response = calculateSearchResults(searchPath.toString());
		
		return response;
	}
	
	public ArrayList<SearchResult> search(String sourceURL){
		String searchPath = sourceURL;
		
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		
		Feed searchFeed = null;
		ExtensibleElement feed = getFeed(searchPath);
		if(feed.getAttributeValue(StringConstants.API_ERROR) == null){			
			if (feed instanceof Feed){
				searchFeed = (Feed)feed;
			}
		}else{
			getApiLogger().debug(feed.getAttributeValue(StringConstants.API_ERROR));
			
		}
		
		if (searchFeed != null) {
			for(Entry entry:  searchFeed.getEntries()) {
				SearchResult result = new SearchResult(entry);
				results.add(result);
			}
		}
		return results;
	}

	protected void addComponent(StringBuffer searchPath ,String component){
		addParameter(searchPath, COMPONENT, component);
	}
	
	protected void addComponents(StringBuffer searchPath ,String components){
		addParameter(searchPath, COMPONENTS, components);
	}
	
	protected void addEmail(StringBuffer searchPath ,String email){
		addParameter(searchPath, EMAIL, email);
	}
	
	protected void addQueryLang(StringBuffer searchPath ,String lang){
		addParameter(searchPath, QUERY_LANG, lang);
	}
	protected void addLang(StringBuffer searchPath ,String lang){
		addParameter(searchPath, LANG, lang);
	}
	protected void addPage(StringBuffer searchPath ,Integer page){
		addParameter(searchPath, PAGE, page);
	}
	
	protected void addPageSize(StringBuffer searchPath ,Integer pageSize){
		addParameter(searchPath, PAGE_SIZE, pageSize);
	}
	
	protected void addQueryText(StringBuffer searchPath ,String queryText){
		addParameter(searchPath, QUERY_TEXT, queryText);
	}
	
	protected void addTag(StringBuffer searchPath ,String tag){
		addParameter(searchPath, TAG, tag);
	}
	
	protected void addUserId(StringBuffer searchPath ,String userid){
		addParameter(searchPath, USER_ID, userid);
	}
	
	private void addSortOrder(StringBuffer catalogPath ,SortOrder sortOrder){
		addParameter(catalogPath, SORT_ORDER, sortOrder);
	}
	
	private void addSortKey(StringBuffer catalogPath ,SortKey sortKey){
		addParameter(catalogPath, SORT_KEY, sortKey);
	}

	private void addScope(StringBuffer catalogPath ,Scope scope){
		addParameter(catalogPath, SCOPE, scope);
	}
	
	
	protected void addFieldConstraints(StringBuffer searchPath ,FieldConstraint[] fieldConstraints){
		if(fieldConstraints != null){
			String fieldConstraintStr = null;
			for(FieldConstraint constraint : fieldConstraints){
				try {
					fieldConstraintStr = URLEncoder.encode(constraint.toString(), UTF_8_ENCODING_TYPE);
					searchPath.append("constraint=");
					searchPath.append(fieldConstraintStr);
					searchPath.append("&");
				} catch (UnsupportedEncodingException e) {
					getApiLogger().debug(e.getMessage());
				}
			}
		}
	}
	
	protected void addFieldNotConstraints(StringBuffer searchPath ,FieldConstraint[] fieldNotConstraints){
		if(fieldNotConstraints != null){
			String fieldNotConstraintStr = null;
			for(FieldConstraint notConstraint : fieldNotConstraints){
				try {
					fieldNotConstraintStr = URLEncoder.encode(notConstraint.toString(), UTF_8_ENCODING_TYPE);
					searchPath.append("notconstraint=");
					searchPath.append(fieldNotConstraintStr);
					searchPath.append("&");
				} catch (UnsupportedEncodingException e) {
					getApiLogger().debug(e.getMessage());
				}
			}
		}
	}
	
	protected void addCategoryConstraints(StringBuffer searchPath ,CategoryConstraint[] categoryConstraints){
		if(categoryConstraints != null){
			String categoryConstraintStr = null;
			for(CategoryConstraint constraint : categoryConstraints){
				try {
					categoryConstraintStr = URLEncoder.encode(constraint.toString(), UTF_8_ENCODING_TYPE);
					searchPath.append("constraint=");
					searchPath.append(categoryConstraintStr);
					searchPath.append("&");
				} catch (UnsupportedEncodingException e) {
					getApiLogger().debug(e.getMessage());
				}
			}
		}
	}
	
	protected void addCategoryNotConstraints(StringBuffer searchPath ,CategoryConstraint[] categoryNotConstraints){
		if(categoryNotConstraints != null){
			String categoryNotConstraintStr = null;
			for(CategoryConstraint notConstraint : categoryNotConstraints){
				try {
					categoryNotConstraintStr = URLEncoder.encode(notConstraint.toString(), UTF_8_ENCODING_TYPE);
					searchPath.append("notconstraint=");
					searchPath.append(categoryNotConstraintStr);
					searchPath.append("&");
				} catch (UnsupportedEncodingException e) {
					getApiLogger().debug(e.getMessage());
				}
			}
		}
	}
	
	
	protected void addRangeConstraints(StringBuffer searchPath ,RangeConstraint[] rangeConstraints){
		if(rangeConstraints != null){
			String rangeConstraintStr = null;
			for(RangeConstraint constraint : rangeConstraints){
				try {
					rangeConstraintStr = URLEncoder.encode(constraint.toString(), UTF_8_ENCODING_TYPE);
					searchPath.append("constraint=");
					searchPath.append(rangeConstraintStr);
					searchPath.append("&");
				} catch (UnsupportedEncodingException e) {
					getApiLogger().debug(e.getMessage());
				}
			}
		}
	}
	
	protected void addRangeNotConstraints(StringBuffer searchPath ,RangeConstraint[] rangeNotConstraints){
		if(rangeNotConstraints != null){
			String rangeNotConstraintStr = null;
			for(RangeConstraint notconstraint : rangeNotConstraints){
				try {
					rangeNotConstraintStr = URLEncoder.encode(notconstraint.toString(), UTF_8_ENCODING_TYPE);
					searchPath.append("notconstraint=");
					searchPath.append(rangeNotConstraintStr);
					searchPath.append("&");
				} catch (UnsupportedEncodingException e) {
					getApiLogger().debug(e.getMessage());
				}
			}
		}
	}
	
	protected void addFactes(StringBuffer searchPath ,Facet[] facets){
		if(facets != null){
			String facetStr = null;
			for(Facet facet : facets){
				try {
					facetStr = URLEncoder.encode(facet.toString(), UTF_8_ENCODING_TYPE);
				} catch (UnsupportedEncodingException e) {
					getApiLogger().debug(e.getMessage());
				}
				searchPath.append("facet=");
				searchPath.append(facetStr);
				searchPath.append("&");
			}
		}
	}
	
	protected void addSocialConstraints(StringBuffer searchPath ,SocialConstraint[] socialConstraints){
		if(socialConstraints != null){
			String socialConstraintStr = null;
			for(SocialConstraint socialConstraint : socialConstraints){
				try {
					socialConstraintStr = URLEncoder.encode(socialConstraint.toString(), UTF_8_ENCODING_TYPE);
				} catch (UnsupportedEncodingException e) {
					getApiLogger().debug(e.getMessage());
				}
				searchPath.append("social=");
				searchPath.append(socialConstraintStr);
				searchPath.append("&");
			}
		}
	}
	
	protected void addStart(StringBuffer searchPath ,Integer start){
		addParameter(searchPath, START, start);
	}
	
	protected void addEvidence(StringBuffer searchPath ,Boolean evidence){
		addParameter(searchPath, EVIDENCE, evidence);
	}
	
	protected void addLocale(StringBuffer searchPath ,Locale locale){
		if(locale != null){
			addParameter(searchPath, LOCALE, locale);
		}
	}
	
	protected void addHighlight(StringBuffer searchPath ,String[] highlights){
		StringBuffer highlightSb = new StringBuffer();
		if(highlights != null){
			String highlightStr = null;
			highlightSb.append("[");
			if (highlights.length != 0){
			for(String highlight : highlights){
				highlightSb.append("\"");
				highlightSb.append(highlight);
				highlightSb.append("\"");
				highlightSb.append(",");
			}
			highlightSb.deleteCharAt(highlightSb.length() - 1);
			}
			highlightSb.append("]");
			try {
				highlightStr="highlight="+URLEncoder.encode(highlightSb.toString(), UTF_8_ENCODING_TYPE);
			} catch (UnsupportedEncodingException e) {
				getApiLogger().debug(e.getMessage());
			}
				searchPath.append(highlightStr);
				searchPath.append("&");
			
		}
	}
	
	protected void addParameter(StringBuffer searchPath ,String paramName, Object paramValue){
		if(paramValue != null){
			searchPath.append(paramName + "=");
			searchPath.append(paramValue);
			searchPath.append("&");
		}	
	}
	
	protected void addSearchRequestEngine(StringBuffer searchPath, SearchRequestEngine searchRequestEngine) {
		if (searchRequestEngine != null) {
			addParameter(searchPath, SEARCH_REQUEST_ENGINE, searchRequestEngine);
		}
	}
	
	protected SearchResponse calculateSearchResults(String searchPath){
		
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		Feed searchFeed = null;

		ExtensibleElement feed = getFeed(searchPath);
		if(feed.getAttributeValue(StringConstants.API_ERROR) == null){
			
			if (feed instanceof Feed){
				searchFeed = (Feed)feed;
			}
		}else{
			getApiLogger().debug(feed.getAttributeValue(StringConstants.API_ERROR));
			return null;
		}
		if (searchFeed != null) {
			for(Entry entry:  searchFeed.getEntries()) {
				SearchResult result = new SearchResult(entry);
				results.add(result);
			}
		}
		
		HashMap<String, Float> tagFacets = getTagFacets(searchFeed);
		HashMap<String, Float> personFacets = getPersonFacets(searchFeed);
		HashMap<String, Float> dateFacets = getDateFacets(searchFeed);
		SearchRequestEngine searchRequestEngine = getSearchRequestEngine(searchFeed);
		return new SearchResponse(results, 
				getRespType(), 
				getRespStatus(), 
				searchRequestEngine, 
				tagFacets, personFacets, dateFacets);
	}


	private SearchRequestEngine getSearchRequestEngine(Feed searchFeed) {
		SearchRequestEngine searchRequestEngine = SearchRequestEngine.Legacy;
		try {
			if (searchFeed != null) {
				String searchRequestEngineString = searchFeed.getSimpleExtension(IBM_XML_NAMESPACE, "searchRequestEngine", "ibmsc");
				if (SearchRequestEngine.Solr.name().equalsIgnoreCase(searchRequestEngineString)) {
					searchRequestEngine = SearchRequestEngine.Solr; 
				} else {
					searchRequestEngine = SearchRequestEngine.Legacy;
					if (!SearchRequestEngine.Legacy.name().equalsIgnoreCase(searchRequestEngineString)) {
						logger.fine("failed to parse SearchRequestEngine: " + searchRequestEngineString + ". Default value returned");
					}
				}				
			}
		} catch (Throwable e) {
			logger.warning("failed to fetch SearchRequestEngine. Default value returned");
		}
		return searchRequestEngine;
	}
	
	private HashMap<String, Float> getTagFacets(Feed searchFeed) {
		HashMap<String, Float> tagFacets = new HashMap<String, Float>();
		try {
			if (searchFeed != null) {
				Element tagFacetElement = getTagFacetElement(searchFeed);
				if (tagFacetElement != null) {
					addTagsToMap(tagFacets, tagFacetElement.getElements());
				}
			}
		} catch (Throwable e) {
			logger.fine("failed to fetch tag facets. Default value returned");
		}
		return tagFacets;
	}
	
	private HashMap<String, Float> getPersonFacets(Feed searchFeed) {
		HashMap<String, Float> personFacets = new HashMap<String, Float>();
		try {
			if (searchFeed != null) {
				Element peopleFacetElement = getPersonFacetElement(searchFeed);
				if (peopleFacetElement != null) {
					addPeopleToMap(personFacets, peopleFacetElement.getElements());
				}
			}
		} catch (Throwable e) {
			logger.warning("failed to fetch person facets. Default value returned");
		}
		return personFacets;
	}
	
	private HashMap<String, Float> getDateFacets(Feed searchFeed) {
		HashMap<String, Float> dateFacets = new HashMap<String, Float>();
		try {
			if (searchFeed != null) {
				Element dateFacetElement = getDateFacetElement(searchFeed);
				if (dateFacetElement != null) {
					addDateToMap(dateFacets, dateFacetElement.getElements());
				}
			}
		} catch (Throwable e) {
			logger.warning("failed to fetch date facets. Default value returned");
		}
		return dateFacets;
	}


	private void addTagsToMap(HashMap<String, Float> tagFacets,
			List<Element> tagElements) {
		if (tagElements == null) {
			return;
		}
		for (Element tagElement : tagElements) {
			String tagName = tagElement.getAttributeValue("label");
			float tagCount = Integer.parseInt(tagElement.getAttributeValue("weight"));
			tagFacets.put(tagName, tagCount);
		}
	}
	
	private void addPeopleToMap(HashMap<String, Float> personFacets,
			List<Element> personElements) {
		if (personElements == null) {
			return;
		}
		for (Element personElement : personElements) {
			String personName = personElement.getAttributeValue("label");
			float personCount = Float.parseFloat(personElement.getAttributeValue("weight"));
			personFacets.put(personName, personCount);
		}
	}
	
	private void addDateToMap(HashMap<String, Float> dateFacets,
			List<Element> dateElements) {
		if (dateElements == null) {
			return;
		}
		for (Element dateElement : dateElements) {
			String personName = dateElement.getAttributeValue("label");
			float personCount = Float.parseFloat(dateElement.getAttributeValue("weight"));
			dateFacets.put(personName, personCount);
		}
	}


	private Element getTagFacetElement(Feed searchFeed) {
		QName facetsElementQName = new QName(IBM_XML_NAMESPACE, "facets", "ibmsc");
		List<Element> facetElements = searchFeed.getExtensions(facetsElementQName);
		Element tagFacetElement = null;
		if (facetElements != null) {
			for (Element facetElement : facetElements.get(0)) {
				String facetName = facetElement.getAttributeValue("id");
				if (facetName.equals("Tag") || facetName.equals("tag"))
					tagFacetElement = facetElement;
			}
		}
		return tagFacetElement;
	}
	
	private Element getPersonFacetElement(Feed searchFeed) {
		QName facetsElementQName = new QName(IBM_XML_NAMESPACE, "facets", "ibmsc");
		List<Element> facetElements = searchFeed.getExtensions(facetsElementQName);
		Element personFacetElement = null;
		if (facetElements != null) {
			for (Element facetElement : facetElements.get(0)) {
				String facetName = facetElement.getAttributeValue("id");
				if (facetName.equalsIgnoreCase("Person"))
					personFacetElement = facetElement;
			}
		}
		return personFacetElement;
	}
	
	private Element getDateFacetElement(Feed searchFeed) {
		QName facetsElementQName = new QName(IBM_XML_NAMESPACE, "facets", "ibmsc");
		List<Element> facetElements = searchFeed.getExtensions(facetsElementQName);
		Element dateFacetElement = null;
		if (facetElements != null) {
			for (Element facetElement : facetElements.get(0)) {
				String facetName = facetElement.getAttributeValue("id");
				if (facetName.equalsIgnoreCase("Date"))
					dateFacetElement = facetElement;
			}
		}
		return dateFacetElement;
	}
	
	public ExtensibleElement getFeed(String url){
		ExtensibleElement response1 = super.getFeed(url);
		if (getRespStatus()== 429){
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				getApiLogger().debug(e.getMessage());
			}
			response1 = super.getFeed(url);
			if (getRespStatus()== 429){
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					getApiLogger().debug(e.getMessage());
				}
				return super.getFeed(url);
			}
			
		}
		if (getRespStatus()== 503){
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				getApiLogger().debug(e.getMessage());
			}
			response1 = super.getFeed(url);
					
		}
		return response1;
		
	}	
}

