package com.ibm.lconn.automation.framework.search.rest.api.population.solr.search.engine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.abdera.model.Entry;
import org.apache.commons.lang3.StringUtils;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.ActivityCreator;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;

public class BoostTestsPopulator {

	public static final String PROXIMITY_FOX_QUICK_TITLE = "fox quick";
	public static final String PROXIMITY_QUICK_BROWN_FOX_TITLE = "quick brown fox";
	public static final String FIELD_BOOST_TEST_QUERY_POSTFIX = "fieldboosttest";
	public static Collection<String> PROXIMITY_BOOST_TITLES = Collections.unmodifiableList(Arrays.asList(new String[] {
			PROXIMITY_FOX_QUICK_TITLE,
			PROXIMITY_QUICK_BROWN_FOX_TITLE }));

	public void createActivitiesForUnifyBoostsTest() throws Exception{
		createActivitiesForFieldBoostTest(Permissions.PRIVATE, Purpose.UNIFY);
		createActivitiesForProximityBoostTest(Permissions.PRIVATE, Purpose.UNIFY);
		createActivitiesForRecencyBoostTest(Permissions.PRIVATE, Purpose.UNIFY);
	}
		
	public void createActivitiesForProximityBoostTest(Permissions permission, Purpose purpose) throws Exception{
		createAndGetActivityForProximityTest(PROXIMITY_FOX_QUICK_TITLE, null, permission, purpose, " ");
		createAndGetActivityForProximityTest(PROXIMITY_QUICK_BROWN_FOX_TITLE, null, permission, purpose, " ");
	}
	
	public void createActivitiesForRecencyBoostTest(Permissions permission, Purpose purpose) throws Exception{
		createAndGetActivity("", null, permission, purpose, "", true, true);
	}
	
	public void createActivitiesForFieldBoostTest(Permissions permission, Purpose purpose) throws Exception{
		//An activity with Id as title is already being created for recency tests
		createAndGetActivity(SearchRestAPIUtils.getExecId(purpose) + FIELD_BOOST_TEST_QUERY_POSTFIX, null, permission, purpose, " ", false, false);
		createAndGetActivity(FIELD_BOOST_TEST_QUERY_POSTFIX, SearchRestAPIUtils.getExecId(purpose) + FIELD_BOOST_TEST_QUERY_POSTFIX, permission, purpose, " ", false, false);
		createAndGetActivity(FIELD_BOOST_TEST_QUERY_POSTFIX, null, permission, purpose, SearchRestAPIUtils.getExecId(purpose) + FIELD_BOOST_TEST_QUERY_POSTFIX, false, false);		
	}
	
	public Entry createAndGetActivity(String titleTemplate, String content, Permissions permission, Purpose purpose,
			String tagName, boolean isExId, boolean addToPopulatedLCEntry) throws Exception{
		Entry response;
		String title =  isExId ? SearchRestAPIUtils.generateTitleFromTemplateString(titleTemplate, purpose) : titleTemplate;
		response = new ActivityCreator().createActivity(StringUtils.stripToEmpty(title), content, tagName, permission, purpose, addToPopulatedLCEntry);
		return response;
	}
	
	public Entry createAndGetActivityForProximityTest(String titleTemplate, String content, Permissions permission, Purpose purpose,
			String tagName) throws Exception{
		Entry response;
		String title =  SearchRestAPIUtils.generateTitleFromTemplateString(titleTemplate, purpose);
		String tag = (tagName!=null ? tagName : SearchRestAPIUtils.generateTagValue(purpose));
		if(content!=null && !content.isEmpty()){
			content = SearchRestAPIUtils.addExecIdToTemplate(content, purpose);
		}
		response = new ActivityCreator().createActivity(title, content, tag, permission, purpose);
		return response;
	}
	
}
