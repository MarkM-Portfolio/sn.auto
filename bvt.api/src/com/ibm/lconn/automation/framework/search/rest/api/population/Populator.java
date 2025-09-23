package com.ibm.lconn.automation.framework.search.rest.api.population;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.apache.abdera.protocol.client.ClientResponse;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.ActivityCreator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.BlogsCreator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.BookmarksCreator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.CommunitiesCreator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.CommunitiesCreatorForQuickResults;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.FileCreator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.ForumCreator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.MyProfileUpdate;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.ProfileCreator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.ProfileUpdate;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.StatusUpdateCreator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.WikiCreator;
import com.ibm.lconn.automation.framework.search.rest.api.population.solr.search.engine.BoostTestsPopulator;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.JapaneseAndEnglishSearchTests;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.JapaneseBasicSearchTests;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.JapaneseEndVowelSearchTests;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.JapaneseHalfAndFullWidthSearchTests;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.JapaneseHiraganaSearchTests;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.JapaneseInflectionSearchTests;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.JapaneseIterationMarkSearchTests;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.JapaneseKanjiAndHiraganaAndKatakanaSearchTests;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.JapaneseKanjiAndHiraganaSearchTests;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.JapaneseKanjiAndKatakanaSearchTests;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.JapaneseKatakanaSearchTests;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.search.request.PeopleFinderRequest;
import com.ibm.lconn.automation.framework.services.search.response.PeopleFinderResponse;
import com.ibm.lconn.automation.framework.services.search.service.PeopleFinderService;



public class Populator {

	public static final String ACTIVITY_TAG_GERMAN = "german";
	
	public static final String ACTIVITY_TAG_SOLR = "solr";

	public static final String ACTIVITY_TAG_STATUS_INACVTIVE = "status=inactive";

	public static String INACTIVE_TAG_FOR_UNIFY = "status=inactive";

	public static String[] UNIFY_ACTIVITY_CATEGORY = { "Source", "activities",
			"activity" };

	public static String[] UNIFY_ACTIVITY_ENTRY_CATEGORY = { "Source",
			"activities", "entry" };

	public static String[] UNIFY_ACTIVITY_REPLY_CATEGORY = { "Source",
			"activities", "reply" };
	public static String[] UNIFY_ACTIVITY_LANG_WORD = { "thought", "think" };
	public static String[] UNIFY_ACTIVITY_LANG_WORD_TWO = { "bring", "brought" };
	public static String[] UNIFY_ACTIVITY_LANG_WORD_THREE = { "foo", "foo" };
	public static String[] UNIFY_ACTIVITY_LANG_WORD_GERMAN = { "Ich mag Katzen", "Katze" };
	public static String[] UNIFY_ACTIVITY_LANG_WORD_GERMAN_SEC = { "mein Hund mag mich", "Hunden" };
	public static String[] UNIFY_ACTIVITY_LANG_WORD_FOUR = { "\u003A\u0029\u003A\u0029\u003A\u0029\u003A\u0029\u0021\u0021\u0021\u0021\u0021\u0021\u0021\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u0077\u006F\u0072\u0064\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A\u002A", "word" };
	public static int UNIFY_RESULTS;
	public static int UNIFY_ENTRIES_NUMBER;
	public static int UNIFY_TODOS_NUMBER;
	public static int UNIFY_SECTIONS_NUMBER;
	public static int UNIFY_REPLIES_NUMBER;
	public static  String COMMUNITY_WITH_WIKI_ATTACHMENT_UUID;
//	public static  String COMMUNITY_WITH_FORUM_ATTACHMENT_UUID;
	public static  String COMMUNITY_WITH_FILE_AND_ACTIVITY_UUID;
	public final static Logger LOGGER_POPUILATOR = Logger
			.getLogger(Populator.class.getName());

	FileHandler fh = null;

	public Populator() throws IOException {
		DateFormat logDateFormatter = (DateFormat) Utils.logDateFormatter.clone();
		fh = new FileHandler("logs/" + logDateFormatter.format(new Date())
				+ "Populator", false);
		LOGGER_POPUILATOR.addHandler(fh);
	}

	private static String getRecommendUserID(){
		try {
			RestAPIUser restAPIUser = new RestAPIUser(UserType.RECOMMEND);
			ServiceEntry searchServiceEntry = restAPIUser.getService("search");
			PeopleFinderService peopleFinderService = new PeopleFinderService(
					restAPIUser.getAbderaClient(), searchServiceEntry);
			PeopleFinderRequest request = new PeopleFinderRequest(restAPIUser.getProfData().getEmail());
			ClientResponse response = peopleFinderService.typeAhead(request);
			PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(response);
			return peopleFinderResponse.getPersons().get(0).getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void populationForCloudAndOnPremise() throws IOException,
			Exception {
		new CommunitiesCreator().createCommunity(Permissions.PRIVATE);
		new ActivityCreator().createActivity(Permissions.PRIVATE);
		new FileCreator().createFile(Permissions.PRIVATE);
		new CommunitiesCreatorForQuickResults().createCommunities();
		new CommunitiesCreator().createCommunity(Permissions.PUBLIC);
		new CommunitiesCreator().createCommunityWithForum(Permissions.PUBLIC, Purpose.SEARCH);
//		new CommunitiesCreator().createCommunityWithForum(Permissions.PRIVATE, Purpose.SEARCH);
		new CommunitiesCreator().createCommunityWithFileAndActivity(Permissions.PRIVATE, Purpose.SEARCH);
		new ActivityCreator().createActivity(Permissions.PUBLIC);
		new ActivityCreator().CreateActivityWithEntryAndAttachmentAndBookmark(Permissions.PUBLIC, Purpose.SEARCH);
		new FileCreator().createFile(Permissions.PUBLIC);
	}

	public static void populationForSolrOnly() throws Exception {
		LOGGER_POPUILATOR.entering(Populator.class.getName(),
				"populationForSolrOnly");
		if (DeploymentType.SMARTCLOUD == StringConstants.DEPLOYMENT_TYPE) {
			populationForUnify();
			//populationAppSolrEngine();
		}
		new CommunitiesCreatorForQuickResults().createCommunities();
		LOGGER_POPUILATOR.exiting(Populator.class.getName(),
				"populationForSolrOnly");
	}

	public static void populationForCloudOnly() throws Exception {
		LOGGER_POPUILATOR.entering(Populator.class.getName(),
				"populationForCloudOnly");
		String recommendUserId = getRecommendUserID();
		new FileCreator().createFile(Permissions.PUBLIC, recommendUserId);
		new CommunitiesCreator().createCommunity(Permissions.PUBLIC, Purpose.RECOMMENDATIONS);
		new CommunitiesCreator().createCommunity(Permissions.PUBLIC, Purpose.RECOMMENDATIONS);
		new CommunitiesCreator().createCommunity(Permissions.PUBLIC, Purpose.RECOMMENDATIONS);
		if (DeploymentType.SMARTCLOUD == StringConstants.DEPLOYMENT_TYPE) {
			populationForUnify();
			populationAppSolrEngine();
		}
		new MyProfileUpdate().populate();
		LOGGER_POPUILATOR.exiting(Populator.class.getName(),
				"populationForCloudOnly");
	}

	public static void populationForRunOnPremise() throws IOException,
			Exception {

		populationForCloudAndOnPremise();
		populationForOnPremiseOnly();
	}

	private static void populationForOnPremiseOnly() throws IOException,
			Exception {
		
		new StatusUpdateCreator().createStatusUpdate();
		new BlogsCreator().createBlog();// No stand alone for SC
		new ForumCreator().createForum(Permissions.PUBLIC);// No stand alone for SC
		new WikiCreator().createWiki(Permissions.PUBLIC); // No stand alone for
		// SC
		new BookmarksCreator().createBookmark(Permissions.PUBLIC); // No stand
		// alone for
		// SC

		new WikiCreator().createWiki(Permissions.PRIVATE);// No stand alone for
		// SC
		new BookmarksCreator().createBookmark(Permissions.PRIVATE); // No stand
		// alone for
		// SC
		new ProfileUpdate().populate();
		new ProfileCreator().createUsersForPeopleFinder();

	}

	private static void populationForUnify() throws Exception {
		ActivityCreator activityCreator = new ActivityCreator();
		UNIFY_ENTRIES_NUMBER = 0;
		UNIFY_TODOS_NUMBER = 0;
		UNIFY_SECTIONS_NUMBER = 0;
		UNIFY_REPLIES_NUMBER = 0;
		
		activityCreator.createActivityWithEntryAndReplyAnd4LangEntryUnify(Permissions.PRIVATE, 
				Purpose.UNIFY, UNIFY_ACTIVITY_LANG_WORD[0], 
				UNIFY_ACTIVITY_LANG_WORD_TWO[0], 
				UNIFY_ACTIVITY_LANG_WORD_THREE[0], 
				UNIFY_ACTIVITY_LANG_WORD_FOUR[0]);
		UNIFY_ENTRIES_NUMBER =UNIFY_ENTRIES_NUMBER + 5;
		UNIFY_REPLIES_NUMBER = UNIFY_REPLIES_NUMBER +1;
		
		activityCreator.createActivityWithSection(Permissions.PRIVATE, 
				Purpose.UNIFY, 
				ACTIVITY_TAG_STATUS_INACVTIVE);
		UNIFY_SECTIONS_NUMBER = UNIFY_SECTIONS_NUMBER +1;
		
		activityCreator.createActivityWithTodo(Permissions.PRIVATE, Purpose.UNIFY, null);
		UNIFY_TODOS_NUMBER = UNIFY_TODOS_NUMBER +1;
		
//		activityCreator.createJapaneseActivityWithTitle(JapaneseKanjiSearchTests.ACTIVITY_LANG_WORD_JAPANESE_BASIC[0], 
//				Permissions.PRIVATE, 
//				Purpose.SEARCH, 
//				UnifySearchTests.ACTIVITY_TAG_SOLR);
		
		activityCreator.createActivityWithTitle(UNIFY_ACTIVITY_LANG_WORD_GERMAN_SEC[0], 
				Permissions.PRIVATE, 
				Purpose.UNIFY,
				ACTIVITY_TAG_GERMAN);
		
		activityCreator.createActivityWithTitle(UNIFY_ACTIVITY_LANG_WORD_GERMAN[0], 
				Permissions.PRIVATE, 
				Purpose.UNIFY,
				ACTIVITY_TAG_GERMAN);
		
		activityCreator.createJapaneseActivityWithTitle(JapaneseBasicSearchTests.ACTIVITY_LANG_WORD_JAPANESE_BASIC_2_INDEX, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseBasicSearchTests.ACTIVITY_LANG_WORD_JAPANESE_BASIC_3_INDEX, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseBasicSearchTests.ACTIVITY_LANG_WORD_JAPANESE_BASIC_4_INDEX, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseBasicSearchTests.ACTIVITY_LANG_WORD_JAPANESE_BASIC_5_INDEX, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseKanjiAndHiraganaAndKatakanaSearchTests.ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_INDEX2, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseHiraganaSearchTests.ACTIVITY_LANG_WORD_JAPANESE_HIRAGANA_INDEX, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseKatakanaSearchTests.ACTIVITY_LANG_WORD_JAPANESE_KATAKANA_INDEX, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseAndEnglishSearchTests.ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX1, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseAndEnglishSearchTests.ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX2, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseAndEnglishSearchTests.ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX3, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseKanjiAndHiraganaAndKatakanaSearchTests.ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_INDEX1, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseKanjiAndHiraganaAndKatakanaSearchTests.ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_INDEX2, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseKanjiAndHiraganaSearchTests.ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_INDEX1, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseKanjiAndKatakanaSearchTests.ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_INDEX1, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseHalfAndFullWidthSearchTests.ACTIVITY_LANG_WORD_JAPANESE_FULL_WIDTH_KATAKANA_AND_LATIN_INDEX, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseHalfAndFullWidthSearchTests.ACTIVITY_LANG_WORD_JAPANESE_FULL_WIDTH_KATAKANA_INDEX, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseHalfAndFullWidthSearchTests.ACTIVITY_LANG_WORD_JAPANESE_HALF_WIDTH_KATAKANA_INDEX, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseInflectionSearchTests.ACTIVITY_LANG_WORD_JAPANESE_INFLECTION_INDEX1, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseInflectionSearchTests.ACTIVITY_LANG_WORD_JAPANESE_INFLECTION_INDEX2, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseInflectionSearchTests.ACTIVITY_LANG_WORD_JAPANESE_INFLECTION_INDEX3, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseEndVowelSearchTests.ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_INDEX1, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseEndVowelSearchTests.ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_INDEX2, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseEndVowelSearchTests.ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_INDEX3, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseIterationMarkSearchTests.ACTIVITY_LANG_WORD_JAPANESE_ITERATION_MARK_INDEX1, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		activityCreator.createJapaneseActivityWithTitle(JapaneseIterationMarkSearchTests.ACTIVITY_LANG_WORD_JAPANESE_ITERATION_MARK_INDEX2, 
				Permissions.PRIVATE, 
				Purpose.JAPANESE, 
				ACTIVITY_TAG_SOLR);
		
		
		new BoostTestsPopulator().createActivitiesForUnifyBoostsTest();
	}
	
	private static void populationAppSolrEngine() throws Exception {
		FileCreator fileCreator = new FileCreator();
		fileCreator.createFile(Permissions.PRIVATE, Purpose.SEARCH_SOLR_ENGINE);
	}
}
