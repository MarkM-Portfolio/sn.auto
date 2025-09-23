package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.facets;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;

public interface SolrLegacyFacetingConstants {
	public final static int NUM_OF_FACETS = 20;
	public final static String SORT_ORDER = "Count";
	public final static String TAG_FIELD_NAME = "Tag";
	public final static String PEOPLE_FIELD = "Person";
	public final static String DATE_FIELD = "Date";
	public final static String[] TAGS = { "lucene2solr-1", "lucene2solr-2" };	
	public final static String[] DATE = { Calendar.getInstance().get(Calendar.YEAR)+"" };
	public final static HashSet<String> expectedTags = new HashSet<String>(Arrays.asList(TAGS[0], TAGS[1], StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.FACETING))));	
	public static final HashSet<String> expectedDate = new HashSet<String>(Arrays.asList(DATE[0]));
}