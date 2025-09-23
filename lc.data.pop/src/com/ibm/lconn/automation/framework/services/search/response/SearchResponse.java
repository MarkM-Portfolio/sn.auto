package com.ibm.lconn.automation.framework.services.search.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.search.nodes.SearchResult;

public class SearchResponse {
	
	public enum SearchRequestEngine {
		Solr, Legacy;
	}

	ArrayList<SearchResult> results;
	ResponseType type;
	int status;
	private final SearchRequestEngine searchRequestEngine;
	private final HashMap<String, Float> tagFacets;
	private final HashMap<String, Float> peopleFacets;
	private final HashMap<String, Float> dateFacets;

	public SearchResponse(ArrayList<SearchResult> results, ResponseType responseType, int responseStatus) {
		this(results, 
				responseType, 
				responseStatus, 
				SearchRequestEngine.Legacy, 
				new HashMap<String, Float>(), new HashMap<String, Float>(), new HashMap<String, Float>());
	}
	
	public SearchResponse(ArrayList<SearchResult> results, 
			ResponseType responseType, 
			int responseStatus, 
			SearchRequestEngine searchRequestEngine,
			HashMap<String, Float> tagFacets, HashMap<String, Float> peopleFacets, HashMap<String, Float> dateFacets) {
		super();
		this.results = results;
		this.type = responseType;
		this.status = responseStatus;
		this.searchRequestEngine = searchRequestEngine;
		this.tagFacets = tagFacets;
		this.peopleFacets = peopleFacets;
		this.dateFacets = dateFacets;
	}

	public ArrayList<SearchResult> getResults() {
		return results;
	}

	public void setResults(ArrayList<SearchResult> results) {
		this.results = results;
	}
	
	public List<SearchResult> getResultsByACL(Permissions permissions){
		List<SearchResult> resultsByACL = new ArrayList<SearchResult>();
		for (SearchResult result : results) {
			String accessControl= result.getAccessControlString();
			if (accessControl.equals(permissions.toString().toLowerCase())){
				resultsByACL.add(result);
			}
		}
		return resultsByACL;
	}

	public int getStatus() {
		return status;
	}

	public ResponseType getType() {
		return type;
	}
	
	public SearchRequestEngine getSearchRequestEngine(){
		return searchRequestEngine;
	}

	/**
	 * Gets entries by permission
	 *  
	 * @param permissions
	 * @return
	 */
	public List <SearchResult> getEntriesByACL(Permissions permissions){
		List<SearchResult> entriesByACL = new ArrayList<SearchResult>();
		for (SearchResult currentResult : results) {
			Category accessControl = currentResult.getAccessControl();
			if (accessControl.getTerm().toLowerCase().equals(permissions.toString().toLowerCase())){
				entriesByACL.add(currentResult);
			}
		}
		return entriesByACL;
	}	
	
	/**
	 * Gets community entries only
	 * @return
	 */
	public List <SearchResult> getCommunityEntries (){
		List<SearchResult> commEntries = new ArrayList<SearchResult>();
		 for (SearchResult currentResult : results) {
			Category documentType = currentResult.getDocumentType();
			if (documentType.getTerm().equals(StringConstants.CATEGORY_TERM_COMMUNITY_APP_GROUP_COMMUNITY)){
				commEntries.add(currentResult);
			}
		}
		return commEntries;
	}
	
	public HashMap<String, Float> getTagFacets() {
		return tagFacets;
	}
	
	public HashMap<String, Float> getPeopleFacets() {
		return peopleFacets;
	}
	
	public HashMap<String, Float> getDateFacets() {
		return dateFacets;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
