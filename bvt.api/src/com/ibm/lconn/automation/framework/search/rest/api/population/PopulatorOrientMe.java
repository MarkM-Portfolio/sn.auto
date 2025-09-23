package com.ibm.lconn.automation.framework.search.rest.api.population;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population.SyncCrawlerBySleeping30Sec;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.ActivityCreator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.CommunitiesCreator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.FileCreator;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;

public class PopulatorOrientMe {
	public static void populationOrientMe() throws Exception {
		new CommunitiesCreator(UserType.ORIENTME,0).createCommunityWithFileAndActivity(Permissions.PUBLIC, Purpose.ORIENTME);
		new CommunitiesCreator(UserType.ORIENTME,0).createCommunityWithWikiAttachment(Permissions.PUBLIC, Purpose.ORIENTME,Purpose.ORIENTME);
		new CommunitiesCreator(UserType.ORIENTME,0).createCommunityWithWikiAndWikiPageinCloud(Permissions.PUBLIC, Purpose.ORIENTME);
		new ActivityCreator(UserType.ORIENTME,0).createActivity(Permissions.PUBLIC, Purpose.ORIENTME);
		new FileCreator(UserType.ORIENTME,0).createFile(Permissions.PUBLIC, Purpose.ORIENTME);
		
		SyncCrawlerBySleeping30Sec.run();
	}
}
