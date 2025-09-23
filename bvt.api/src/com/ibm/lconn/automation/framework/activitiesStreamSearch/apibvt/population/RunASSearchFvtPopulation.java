package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

public class RunASSearchFvtPopulation {

	public static void populate() throws Exception {

		new ASCommunitiesPopulationHelper().populate();

		new ASDogearPopulationHelper().populate();

		new ASForumsPopulationHelper().populate();

		new ASProfilesPopulationHelper().populate();

		new ASActivitiesPopulationHelper().populate();

		new ASBlogsPopulationHelper().populate();

		new ASFilesPopulationHelper().populate();

		new ASWikisPopulationHelper().populate();

		new ASUblogsPopulationHelper().createMentionsMessagesToMySelfAndOther();

		ASCustomListPopulationHelper.getInstance().populate();

	}
	public static void populateForCloud() throws Exception {

		new ASCommunitiesPopulationHelper().createPrivateCommunity();
		new ASCommunitiesPopulationHelper().createPublicCommunity();
		new ASCommunitiesPopulationHelper().createModeratedCommunity();
		new ASCommunitiesPopulationHelper().createPublicFrenchCommunityUnicode();
		new ASCommunitiesPopulationHelper().createPrivateFrenchCommunity2Unicode();

		

		
		
		

		

	}
	

}