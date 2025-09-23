package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

/*
 * 16.04.2012 - Yakov Vilenchik
 * Add possibility to add SUFFIX as parameter from command line, if such parameter is not provoded the default parameter
 * (SUFFIX)will be used, if parameter is passed as random then random number (0-100000) will be choosed
 * if parameter is passed as number then this number is added as SUFFIX to every string
 * 09.12.13 - workspace encoding changed to UTF-8
 */

public class PopStringConstantsAS {

	public static String SERVER_URL = URLConstants.SERVER_URL;

	public final static String SUFFIX = "";

	public static String TITLE_SUFFIX = "";

	public static String SUB_COMMUNITY_SUFFIX = "subway";

	public final static String ASSEARCH_SUFFIX = "assearch";

	public static int RECEIVED_PAGE_SIZE = 50;

	public static String TEST_EVENTS_IDENTIFIER = "";

	public static String[] unsearchableIndexEvents = {};// List of events that

	// exists in index but
	// not in db.
	// public final static String TITLE_SUFFIX =
	// " suffix"+random.nextInt(99999); // suffix for keeping titles unique

	// in case when executing population and testing from one
	// lc.data.pop/src/com/ibm/lconn/automation/framework/activitiesStreamSearch/fvtPopulation/Main.class
	@SuppressWarnings("unused")
	private final static Random random = new Random();

	// Creation of event Identifier to allow multiple creations of event
	static DateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");

	public static String eventIdent = formatter.format(new Date());

	// Helper Enum Types
	public enum CommunityBlogPermissions {
		PUBLIC, PRIVATE, MODERATED
	}

	public enum Role {
		ALL, AUTHOR, OWNER, MEMBER, READER
	}

	// public static String SERVER_URL = "https://lc4yakov1.haifa.ibm.com";

	/*
	 * public static void setServerURL(String url) { SERVER_URL = url; }
	 */

	// Server Credentials
	public static String USER_NAME; // user for population

	public static String USER_PASSWORD;

	public static String USER_NAME_TEST = PopStringConstantsAS.USER_NAME; // user

	// for
	// test

	public static String USER_PASSWORD_TEST = PopStringConstantsAS.USER_PASSWORD;

	public static String USER_EMAIL;

	public static String USER_DISPLAY_NAME = StringConstants.USER_REALNAME;

	public static String WAS_ADMIN_USER_NAME = "wasadmin"; // was admin user -

	// to check china
	// server set from
	// wasadmin to
	// lcuser

	public static String WAS_ADMIN_USER_PASSWORD = "lcsecret"; // for test

	// changed from
	// lcsecret to
	// wasadmin, on
	// cina server
	// passw0rd

	public static final String TARGET_USER_EMAIL = "fbrion@renovations.com";

	public static final String SECOND_TARGET_USER_EMAIL = "aalain@renovations.com";

	public static final String DB_SEARCH_PARAMETER = "?rollup=false";

	public static final String INDEX_SEARCH_PARAMETER = "?preferSearchIndex=true";

	public static void setLoginUserName(String user) {
		USER_NAME = user;
	}

	public static void setLoginUserPwd(String password) {
		USER_PASSWORD = password;
	}

	public static void setLoginUserEmail(String email) {
		USER_EMAIL = email;
	}

	public static void setLoginUserRealName(String realName) {
		USER_DISPLAY_NAME = realName;
	}

	public static void setTestUserName(String user) {
		USER_NAME_TEST = user;
	}

	public static void setTestUserPwd(String password) {
		USER_PASSWORD_TEST = password;
	}

	// Activities Constant Strings
	public static final String MODERATED_COMMUNITY_ACTIVITY_TITLE = "First Manager Auction tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_COMMUNITY_ACTIVITY_TITLE = "My First Auction tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACTIVITY_TITLE = "First everybody Auction tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String MODERATED_COMMUNITY_ACTIVITY_CONTENT = "Thirteen"
			+ SUFFIX;

	public static final String PRIVATE_COMMUNITY_ACTIVITY_CONTENT = "Fourteen"
			+ SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACTIVITY_CONTENT = "Fifteen"
			+ SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_TITLE = "First everybody standalone Auction tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_PRIVATE_ACTIVITY_TITLE = "My First Standalone Auction tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_CONTENT = "Sixteen"
			+ SUFFIX;

	public static final String STANDALONE_PRIVATE_ACTIVITY_CONTENT = "Seventeen"
			+ SUFFIX;

	public static final String MODERATED_COMMUNITY_ACTIVITY_ENTRY_TITLE = "First Manager Start tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_COMMUNITY_ACTIVITY_ENTRY_TITLE = "My First Start tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACTIVITY_ENTRY_TITLE = "Everybody First Start tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String MODERATED_COMMUNITY_ACTIVITY_ENTRY_CONTENT = "Eighteen"
			+ SUFFIX;

	public static final String PRIVATE_COMMUNITY_ACTIVITY_ENTRY_CONTENT = "Nineteen"
			+ SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACTIVITY_ENTRY_CONTENT = "Twenty"
			+ SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACTIVITY_PRIVATE_ENTRY_TITLE = "Everybody Second Start Hide tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACTIVITY_PRIVATE_ENTRY_CONTENT = "Twenty one"
			+ SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_ENTRY_TITLE = "Everybody First Start Standalone tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_PRIVATE_ACTIVITY_ENTRY_TITLE = "My First Start standalone tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_ENTRY_CONTENT = "Twenty three"
			+ SUFFIX;

	public static final String STANDALONE_PRIVATE_ACTIVITY_ENTRY_CONTENT = "Twenty two"
			+ SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_PRIVATE_ENTRY_TITLE = "Everybody Second Start Standalone Hide tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_PRIVATE_ENTRY_CONTENT = "Twenty four"
			+ SUFFIX;

	public static final String MODERATED_COMMUNITY_ACTIVITY_ENTRY_COMMENT_TITLE = "First Manager Start tiger remark"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String MODERATED_COMMUNITY_ACTIVITY_ENTRY_COMMENT_Content = "Twenty five"
			+ SUFFIX;

	public static final String PRIVATE_COMMUNITY_ACTIVITY_ENTRY_COMMENT_TITLE = "My First Start tiger remark"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_COMMUNITY_ACTIVITY_ENTRY_COMMENT_Content = "Twenty six"
			+ SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACTIVITY_ENTRY_COMMENT_TITLE = "Everybody First Start tiger remark"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACTIVITY_ENTRY_COMMENT_Content = "Twenty seven"
			+ SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACTIVITY_ENTRY_PRIVATE_COMMENT_TITLE = "Everybody First Start tiger HideRemark Two"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACTIVITY_ENTRY_PRIVATE_COMMENT_CONTENT = "Twenty eight"
			+ SUFFIX;

	public static final String STANDALONE_PRIVATE_ACTIVITY_ENTRY_COMMENT_TITLE = "My First Start standalone tiger Remark"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_PRIVATE_ACTIVITY_ENTRY_COMMENT_CONTENT = "Twenty nine"
			+ SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_ENTRY_COMMENT_TITLE = "Everybody First Start standalone tiger Remark"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_ENTRY_COMMENT_CONTENT = "Thirty"
			+ SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_ENTRY_PRIVATE_COMMENT_TITLE = "Everybody First Start standalone tiget Remark Hide two"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_ENTRY_PRIVATE_COMMENT_CONTENT = "Thirty one"
			+ SUFFIX;

	public static final String MODERATED_COMMUNITY_AVTIVITY_TODO_TITLE = "First Manager Duty tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String MODERATED_COMMUNITY_AVTIVITY_TODO_CONTENT = "Thirty two"
			+ SUFFIX;

	public static final String PRIVATE_COMMUNITY_AVTIVITY_TODO_TITLE = "My First Duty tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_COMMUNITY_AVTIVITY_TODO_CONTENT = "Thirty three"
			+ SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACVTIVITY_TODO_TITLE = "Everybody First Duty tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACVTIVITY_TODO_CONTENT = "Thirty four"
			+ SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACVTIVITY_PRIVATE_TODO_TITLE = "Everybody Second Duty tiger Hide"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_ACVTIVITY_PRIVATE_TODO_CONTENT = "Thirty five"
			+ SUFFIX;

	public static final String STANDALONE_PRIVATE_ACTIVITY_TODO_TITLE = "My First Duty tiger Standalone"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_PRIVATE_ACTIVITY_TODO_CONTENT = "Thirty six"
			+ SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_TODO_TITLE = "Everybody First Duty tiger Standalone"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_TODO_CONTENT = "Thirty seven"
			+ SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_PRIVATE_TODO_TITLE = "Everybody Second Duty tiger Standalone Hide"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_PUBLIC_ACTIVITY_PRIVATE_TODO_CONTENT = "Thirty eight"
			+ SUFFIX;

	public static final String MODERATED_COMMUNITY_ACTIVITY_TAG = "tag8";

	public static final String PRIVATE_COMMUNITY_ACTIVITY_TAG = "tag9";

	public static final String PUBLIC_COMMUNITY_ACTIVITY_TAG = "tag10";

	public static final String STANDALONE_PUBLIC_ACTIVITY_TAG = "tag11";

	public static final String STANDALONE_PRIVATE_ACTIVITY_TAG = "tag12";

	public static final String STANDALONE_PUBLIC_ACTIVITY_ENTRY_TAG = "tag13";

	public static final String STANDALONE_PRIVATE_ACTIVITY_ENTRY_TAG = "tag14";

	public static final String STANDALONE_PUBLIC_ACTIVITY_PRIVATE_ENTRY_TAG = "tag15";

	public static final String STANDALONE_PRIVATE_ACTIVITY_TODO_TAG = "tag16";

	public static final String STANDALONE_PUBLIC_ACTIVITY_TODO_TAG = "tag17";

	public static final String STANDALONE_PUBLIC_ACTIVITY_PRIVATE_TODO_TAG = "tag18";

	// Communities Constant Strings
	public static final String MODERATED_COMMUNITY_TITLE = "First Manager team tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_COMMUNITY_TITLE = "My first team tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_TITLE = "Everybody first team tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String MODERATED_COMMUNITY_BOOKMARK_TITLE = "First Manager Page tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_COMMUNITY_BOOKMARK_TITLE = "First My Page tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_BOOKMARK_TITLE = "First everybody Page tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String MODERATED_COMMUNITY_TOPIC_TITLE = "First Manager item tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_COMMUNITY_TOPIC_TITLE = "First My Item tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_TOPIC_TITLE = "First Everybody Item tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String MODERATED_COMMUNITY_CONTENT = "One" + SUFFIX;

	public static final String PRIVATE_COMMUNITY_CONTENT = "Two" + SUFFIX;

	public static final String PUBLIC_COMMUNITY_CONTENT = "Three" + SUFFIX;

	public static final String MODERATED_COMMUNITY_BOOKMARK_CONTENT = "Four"
			+ SUFFIX;

	public static final String PRIVATE_COMMUNITY_BOOKMARK_CONTENT = "Five"
			+ SUFFIX;

	public static final String PUBLIC_COMMUNITY_BOOKMARK_CONTENT = "Six"
			+ SUFFIX;

	public static final String MODERATED_COMMUNITY_TOPIC_CONTENT = "Seven"
			+ SUFFIX;

	public static final String PRIVATE_COMMUNITY_TOPIC_CONTENT = "Eight"
			+ SUFFIX;

	public static final String PUBLIC_COMMUNITY_TOPIC_CONTENT = "Nine" + SUFFIX;

	public static final String PUBLIC_COMMUNITY_1_TITLE = "Mon français équipes tigres et des gâteaux"
			+ TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_1_CONTENT = "Evolution et révolution dans leurs poupées"
			+ SUFFIX;

	// For utf-8 issue test
	public static final String PUBLIC_COMMUNITY_1_TITLE_UNICODE = "\u004d\u006f\u006e\u0020\u0066\u0072\u0061\u006e\u00e7\u0061\u0069\u0073\u0020\u00e9\u0071\u0075\u0069\u0070\u0065\u0073\u0020\u0074\u0069\u0067\u0072\u0065\u0073\u0020\u0065\u0074\u0020\u0064\u0065\u0073\u0020\u0067\u00e2\u0074\u0065\u0061\u0075\u0078"
			+ TITLE_SUFFIX + " UNICODE";

	public static final String PUBLIC_COMMUNITY_1_CONTENT_UNICODE = "\u0045\u0076\u006f\u006c\u0075\u0074\u0069\u006f\u006e\u0020\u0065\u0074\u0020\u0072\u00e9\u0076\u006f\u006c\u0075\u0074\u0069\u006f\u006e\u0020\u0064\u0061\u006e\u0073\u0020\u006c\u0065\u0075\u0072\u0073\u0020\u0070\u006f\u0075\u0070\u00e9\u0065\u0073"
			+ SUFFIX + " UNICODE";

	public static final String MODERATED_COMMUNITY_TAG = "tag1";

	public static final String PRIVATE_COMMUNITY_TAG = "tag2";

	public static final String PUBLIC_COMMUNITY_TAG = "tag3";

	public static final String PUBLIC_COMMUNITY_1_TAG = "tag4";

	public static final String MODERATED_COMMUNITY_BOOKMARK_TAG = "tag5";

	public static final String PRIVATE_COMMUNITY_BOOKMARK_TAG = "tag6";

	public static final String PUBLIC_COMMUNITY_BOOKMARK_TAG = "tag7";

	// Dogear (Bookmarks) Constant Strings
	public static final String PUBLIC_STANDALONE_BOOKMARK_TITLE = "Second everybody Page tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_STANDALONE_BOOKMARK_TITLE = "Second My Page tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_STANDALONE_BOOKMARK_CONTENT = "Flower flower"
			+ SUFFIX;

	public static final String PRIVATE_STANDALONE_BOOKMARK_CONTENT = "Flower flower"
			+ SUFFIX;

	public static final String PUBLIC_STANDALONE_BOOKMARK_TAG = "tag19";

	public static final String PRIVATE_STANDALONE_BOOKMARK_TAG = "tag20";

	// Forums Constant Strings
	public static final String STANDALONE_FORUM_TITLE = "Everybody Second Hall tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_FORUM_CONTENT = "Ten" + SUFFIX;

	public static final String STANDALONE_FORUM_TOPIC_TITLE = "Everybody Second Hall Item tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_FORUM_TOPIC_CONTENT = "Eleven"
			+ SUFFIX;

	public static final String STANDALONE_FORUM_TOPIC_RESPONSE_TITLE = "Everybody Second Hall Item tiger Reply"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_FORUM_TOPIC_RESPONSE_CONTENT = "Ninety nine"
			+ SUFFIX;

	// Blogs Constant Strings
	public static final String MODERATED_COMMUNITY_BLOG_TITLE = "First Manager tiger Place"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String MODERATED_COMMUNITY_BLOG_CONTENT = "Content1"
			+ SUFFIX;

	public static final String PRIVATE_COMMUNITY_BLOG_TITLE = "My First tiger Place"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_COMMUNITY_BLOG_CONTENT = "Content2"
			+ SUFFIX;

	public static final String PUBLIC_COMMUNITY_BLOG_TITLE = "Everybody First tiger Place"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_BLOG_CONTENT = "Content3"
			+ SUFFIX;

	public static final String STANDALONE_BLOG_TITLE = "The First tiger Place"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_BLOG_CONTENT = "Thirty nine" + SUFFIX;

	public static final String MODERATED_COMMUNITY_BLOG_ENTRY_TITLE = "Manager First Story tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String MODERATED_COMMUNITY_BLOG_ENTRY_CONTENT = "Forty"
			+ SUFFIX;

	public static final String PRIVATE_COMMUNITY_BLOG_ENTRY_TITLE = "My First Story tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_COMMUNITY_BLOG_ENTRY_CONTENT = "Forty one"
			+ SUFFIX;

	public static final String PUBLIC_COMMUNITY_BLOG_ENTRY_TITLE = "Everybody First Story tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_BLOG_ENTRY_CONTENT = "Forty two"
			+ SUFFIX;

	public static final String STANDALONE_BLOG_ENTRY_TITLE = "The Team tiger First Story tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_BLOG_ENTRY_CONTENT = "Forty three"
			+ SUFFIX;

	public static final String STANDALONE_BLOG_ENTRY_COMMENT_CONTENT = "The Team tiger First remark to story"
			+ SUFFIX;

	public static final String STANDALONE_BLOG_RECOMMENDED_ENTRY_TITLE = "Recommended Entry Standalone Title"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String STANDALONE_BLOG_RECOMMENDED_ENTRY_CONTENT = "Recommended Entry Standalone Content"
			+ SUFFIX;

	public static final String MODERATED_COMMUNITY_BLOG_TAG = "tag21";

	public static final String PRIVATE_COMMUNITY_BLOG_TAG = "tag22";

	public static final String PUBLIC_COMMUNITY_BLOG_TAG = "tag23";

	public static final String STANDALONE_BLOG_TAG = "tag24";

	public static final String MODERATED_COMMUNITY_BLOG_ENTRY_TAG = "tag25";

	public static final String PRIVATE_COMMUNITY_BLOG_ENTRY_TAG = "tag26";

	public static final String PUBLIC_COMMUNITY_BLOG_ENTRY_TAG = "tag27";

	public static final String STANDALONE_BLOG_ENTRY_TAG = "tag28";

	// For highlight test case testing
	public static final String HIGHLIGHT_TC_BLOG_TITLE = "one two&three" + " "
			+ ASSEARCH_SUFFIX;

	public static final String HIGHLIGHT_TC_BLOG_TITLE_2 = "four five@six"
			+ " " + ASSEARCH_SUFFIX;

	public static final String HIGHLIGHT_TC_BLOG_TITLE_3 = "seven eight$nine"
			+ " " + ASSEARCH_SUFFIX;

	// Ublogs Constant Strings
	public static final String UBLOG_STRING = "Test ublog" + SUFFIX;

	public static final String UBLOG_COMMENT = "Test ublog comment" + SUFFIX;

	public static final String UBLOGCOMMUNITY_TITLE_SP = " ublog test community"
			+ " " + ASSEARCH_SUFFIX + SUFFIX;

	// Files Constant Strings
	public static final String PICTURE1_PATH = "C:\\lc4_code\\pictures\\lamborghini_murcielago_lp640.jpg";

	public static final String MODERATED_COMMUNITY_FILE_PATH = "dogs.txt";

	public static final String MODERATED_COMMUNITY_FILE_NAME = "dogs.txt" + " "
			+ ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String MODERATED_COMMUNITY_FILE_TAG = "tag29";

	public static final String PRIVATE_COMMUNITY_FILE_PATH = "cats.txt";

	public static final String PRIVATE_COMMUNITY_FILE_NAME = "cats.txt" + " "
			+ ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_COMMUNITY_FILE_TAG = "tag30";

	public static final String PRIVATE_STANDALONE_FILE_PATH = "parrots.txt";

	public static final String PRIVATE_STANDALONE_FILE_NAME = "parrots.txt"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_STANDALONE_FILE_TAG = "tag31";

	public static final String PUBLIC_COMMUNITY_FILE_PATH = "fish.txt";

	public static final String PUBLIC_COMMUNITY_FILE_NAME = "fish.txt" + " "
			+ ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_FILE_TAG = "tag32";

	public static final String PUBLIC_STANDALONE_FILE_PATH = "monkey.txt";

	public static final String PUBLIC_STANDALONE_FILE_NAME = "monkey.txt" + " "
			+ ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_STANDALONE_FILE_TAG = "tag33";

	public static final String TAG_TO_ADD_FILE = "pets" + SUFFIX;

	public static final String COMMENT_TO_MODERATED_COMMUNITY_FILE = "Comment to dogs file"
			+ " " + ASSEARCH_SUFFIX + SUFFIX;

	public static final String COMMENT_TO_PRIVATE_COMMUNITY_FILE = "Comment to cats file"
			+ " " + ASSEARCH_SUFFIX + SUFFIX;

	public static final String COMMENT_TO_PUBLIC_COMMUNITY_FILE = "Comment to fish file"
			+ " " + ASSEARCH_SUFFIX + SUFFIX;

	public static final String COMMENT_TO_PRIVATE_STANDALONE_FILE = "Comment to parrots file"
			+ " " + ASSEARCH_SUFFIX + SUFFIX;

	public static final String COMMENT_TO_PUBLIC_STANDALONE_FILE = "Comment to monkey file"
			+ " " + ASSEARCH_SUFFIX + SUFFIX;

	public static final String FILE_CONTENT = "This is cool file!";

	public static final String FILE_CONTENT_UPDATE_WORD = "ubique";

	// Wikis Constant Strings
	public static final String PRIVATE_STANDALONE_WIKI_TITLE = "My first standalone Novel tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_STANDALONE_WIKI_CONTENT = "Fifty three"
			+ SUFFIX;

	public static final String PUBLIC_STANDALONE_WIKI_TITLE = "Everybody first standalone Novel tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_STANDALONE_WIKI_CONTENT = "Fifty four"
			+ SUFFIX;

	public static final String MODERATED_COMMUNITY_WIKI_TITLE = "Moderated Community Wiki"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String MODERATED_COMMUNITY_WIKI_CONTENT = "Fifty five"
			+ SUFFIX;

	public static final String MODERATED_COMMUNITY_WIKI_PAGE_TITLE = "Manager First Chapter tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_COMMUNITY_WIKI_TITLE = "Private Community Wiki"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_COMMUNITY_WIKI_CONTENT = "Fifty six"
			+ SUFFIX;

	public static final String PRIVATE_COMMUNITY_WIKI_PAGE_TITLE = "My First Chapter tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_WIKI_TITLE = "Public Community Wiki"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_COMMUNITY_WIKI_CONTENT = "Fifty eight"
			+ SUFFIX;

	public static final String PUBLIC_COMMUNITY_WIKI_PAGE_TITLE = "Everybody First Chapter tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_STANDALONE_WIKI_PAGE_TITLE = "My First Standalone Chapter tiger"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PRIVATE_STANDALONE_WIKI_PAGE_CONTENT = "Fifty seven"
			+ SUFFIX;

	public static final String PUBLIC_STANDALONE_WIKI_PAGE_TITLE = "Everybody First Chapter tiger standalone"
			+ " " + ASSEARCH_SUFFIX + TITLE_SUFFIX;

	public static final String PUBLIC_STANDALONE_WIKI_PAGE_CONTENT = "Fifty nine"
			+ SUFFIX;

	public static final String PRIVATE_STANDALONE_WIKI_PAGE_TAG = "birds"
			+ SUFFIX;

	public static final String PUBLIC_STANDALONE_WIKI_PAGE_TAG = "birds"
			+ SUFFIX;

	public static final String PRIVATE_STANDALONE_WIKI_TAG = "tag34";

	public static final String PUBLIC_STANDALONE_WIKI_TAG = "tag35";

	// Profiles Constant Strings
	public static final String PROFILE_TAG1 = "birds" + SUFFIX;

	public static final String PROFILE_TAG2 = "persons" + SUFFIX;

	// public static final String PROFILE_STATUS_UPDATE =
	// "Jean-Michel Aulas brillait par sa discrétion ces dernières semaines. Mais certains sujets ont le mérite de réveiller ce fauve des micros et l'arbitrage en fait partie quand il estime que son OL a été désavantagé. C'était visiblement le cas après le nul (4-4) de samedi soir. \"Je suis indigné : toutes les actions litigieuses ont été sifflées dans le même sens, a-t-il déclaré à chaud. J’ai essayé d’avoir du recul mais comment peut-on siffler quatre minutes de temps additionnel ? C’est impossible... Il y a une douzaine d’actions litigieuses, il y a une faute sur l’un de nos joueurs sur le troisième but parisien. Je suis déçu et malheureux, je m’excuse car le spectacle n’a pas été objectif. Lyon a été complètement désavantagé, je ne sais pas pourquoi ni dans quel intérêt."
	// + SUFFIX;
	// public static final String PROFILE_STATUS_UPDATE_COMMENT =
	// "communiqué de presse aujourd'hui" + SUFFIX;
	public static final String PROFILE_STATUS_UPDATE_UNICODE = "\u004a\u0065\u0061\u006e\u002d\u004d\u0069\u0063\u0068\u0065\u006c\u0020\u0041\u0075\u006c\u0061\u0073\u0020\u0062\u0072\u0069\u006c\u006c\u0061\u0069\u0074\u0020\u0070\u0061\u0072\u0020\u0073\u0061\u0020\u0064\u0069\u0073\u0063\u0072\u00e9\u0074\u0069\u006f\u006e\u0020\u0063\u0065\u0073\u0020\u0064\u0065\u0072\u006e\u0069\u00e8\u0072\u0065\u0073\u0020\u0073\u0065\u006d\u0061\u0069\u006e\u0065\u0073\u002e\u0020\u004d\u0061\u0069\u0073\u0020\u0063\u0065\u0072\u0074\u0061\u0069\u006e\u0073\u0020\u0073\u0075\u006a\u0065\u0074\u0073\u0020\u006f\u006e\u0074\u0020\u006c\u0065\u0020\u006d\u00e9\u0072\u0069\u0074\u0065\u0020\u0064\u0065\u0020\u0072\u00e9\u0076\u0065\u0069\u006c\u006c\u0065\u0072\u0020\u0063\u0065\u0020\u0066\u0061\u0075\u0076\u0065\u0020\u0064\u0065\u0073\u0020\u006d\u0069\u0063\u0072\u006f\u0073\u0020\u0065\u0074\u0020\u006c\u0027\u0061\u0072\u0062\u0069\u0074\u0072\u0061\u0067\u0065\u0020\u0065\u006e\u0020\u0066\u0061\u0069\u0074\u0020\u0070\u0061\u0072\u0074\u0069\u0065\u0020\u0071\u0075\u0061\u006e\u0064\u0020\u0069\u006c\u0020\u0065\u0073\u0074\u0069\u006d\u0065\u0020\u0071\u0075\u0065\u0020\u0073\u006f\u006e\u0020\u004f\u004c\u0020\u0061\u0020\u00e9\u0074\u00e9\u0020\u0064\u00e9\u0073\u0061\u0076\u0061\u006e\u0074\u0061\u0067\u00e9\u002e";

	public static final String PROFILE_STATUS_UPDATE_COMMENT_UNICODE = "\u0063\u006f\u006d\u006d\u0075\u006e\u0069\u0071\u0075\u00e9\u0020\u0064\u0065\u0020\u0070\u0072\u0065\u0073\u0073\u0065\u0020\u0061\u0075\u006a\u006f\u0075\u0072\u0064\u0027\u0068\u0075\u0069"
			+ SUFFIX;

	public static final String PROFILE_STATUS_UPDATE_1 = "Everybody First History"
			+ SUFFIX;

	public static final String PROFILE_STATUS_UPDATE_1_REPLAY = "Wall paper First History"
			+ SUFFIX;

	public static final String PROFILE_STATUS_UPDATE_2 = "Everybody Second History"
			+ SUFFIX;

	public static String PROFILE_STATUS_UPDATE_2_REPLAY = "Wall paper Second History"
			+ SUFFIX;

	// JSON objects location - General
	public static final String EVENT_TITLE_LOCATION = "openSocial.embed.context.title";

	public static final String EVENT_TYPE_LOCATION = "openSocial.embed.context.eventType";

	public static final String EVENT_TITLE_LOCATION_ALT = "object.displayName";

	public static final String EVENT_TITLE_LOCATION_ALT_1 = "title";

	public static final String JSON_ROOT_ELEMENT = "$.";

	public static final String JSON_CURRENT_ELEMENT = "@.";

	public static final String JSON_ROOT_ENTRY = "list";

	public static final String EVENT_SUMMARY_LOCATION = "openSocial.embed.context.summary";

	public static final String EVENT_SUMMARY_LOCATION_ALT = "object.summary";

	public static final String EVENT_ID_LOCATION = "openSocial.embed.context.eventId";

	public static final String TITLE_LOCATION_FOR_SEARCH = "title";

	public static final String TOP_COMMUNITIES_LOCATION = "connections.facets.communities";

	public static final String TOP_PEOPLE_LOCATION = "connections.facets.people";

	public static final String FACETS_ID_LOCATION = "id";

	public static final String FACETS_LABEL_LOCATION = "label";

	public static final String FACETS_SCORE_LOCATION = "score";

	public static final String SNAPSHOT_LOCATION = "connections.snapshot";

	public static final String PUBLISHED_LOCATION = ".published";

	public static final String TARGET_PERSON_ELEMENT_LOCATION = "target.author.displayName";

	// For events with @Mentions that added at 18.01.2013
	public static final String TARGET_PERSON_ELEMENT_LOCATION_MENTIONS = "target.displayName";

	public static final String TARGET_PERSON_ELEMENT_OBJECT_TYPE_MENTIONS_LOC = "target.objectType";

	public static final String TARGET_PERSON_ELEMENT_OBJECT_TYPE_MENTIONS = "person";

	public static final String TARGET_PERSON_ID_LOCATION_MENTIONS = "target.id";

	// Communities JSON objects
	public static final String COMMUNITY_CREATION_EVENT_TYPE = "community.created";

	public static final String COMMUNITY_CONTAINER_ID_LOCATION = "connections.containerId";

	public static final String COMMUNITY_ID_LOCATION = "connections.communityid";

	public static final String SUBCOMMUNITY_CREATION_EVENT_TYPE = "community.subcommunity.created";

	// Files JSON objects
	public static final String FILES_CREATION_EVENT_TYPE = "files.file.updated";

	// Forum JSON objects
	public static final String FORUM_CREATION_EVENT_TYPE = "forum.created";

	public static final String FORUM_TOPIC_CREATION_EVENT_TYPE = "forum.topic.created";

	public static final String TOPIC_REPLY_CREATION_EVENT_TYPE = "forum.topic.reply.created";

	// Bookmarks JSON objects
	public static final String COMMUNITY_BOOKMARK_CREATION_EVENT_TYPE = "community.bookmark.created";

	public static final String STANDALONE_BOOKMARK_CREATION_EVENT_TYPE = "dogear.bookmark.added";

	public static final String STANDALONE_BOOKMARK_CREATION_EVENT_TYPE1 = "dogear.bookmark.edited";

	// Blogs JSON objects
	public static final String BLOG_ENTRY_CREATION_EVENT_TYPE = "blog.entry.created";

	public static final String BLOG_CREATION_EVENT_TYPE = "blog.created";

	// Activities JSON objects
	public static final String ACTIVITY_CREATION_ENTRY_TYPE = "activity.created";

	public static final String STANDALONE_PUBLIC_ACTIVITY_CREATION_EVENT_TYPE = "activity.visibility.updated";

	public static final String ACTIVITY_REPLY_CREATION_EVENT = "activity.reply.created";

	public static final String ACTIVITY_ENTRY_CREATION_EVENT = "activity.entry.created";

	public static final String ACTIVITY_TODO_CREATION_EVENT = "activity.todo.created";

	// Wikis JSON objects
	public static final String WIKI_CREATION_EVENT_TYPE = "wiki.library.created";

	public static final String WIKI_PAGE_CREATION_EVENT_TYPE = "wiki.page.created";

	public static final String WIKI_PAGE_ADD_TAG_EVENT_TYPE = "wiki.page.tag.added";

	// User JSON objects
	public static final String ENTRY_USER_DISPLAY_NAME_LOCATION = "actor.displayName";

	public static final String ENTRY_USER_USER_ID_LOCATION = "actor.id";

	// Mentions JSON objects
	public static final String MENTIONS_SECOND_USER_LOGIN = StringConstants.RANDOM1_USER_NAME;

	public static final String MENTIONS_SECOND_USER_PASSWORD = StringConstants.RANDOM1_USER_PASSWORD;

	public static final String MENTIONS_OTHER_USER = StringConstants.RANDOM1_USER_REALNAME;

	public static final String MENTIONS_EVENT_TYPE_1 = "community.wall.notification.mention";

	public static final String MENTIONS_EVENT_TYPE_2 = "profiles.status.notification.mention";

	public static final String MENTIONS_EVENT_TYPE_3 = "profiles.wallpost.notification.mention";

	// public static final String MENTIONS_USER_MY_SELF = "@Amy Jones242";
	// public static final String MENTIONS_USER_OTHER = "@Amy Jones305";
	public static final String USER_ID_PREFIX = "urn:lsid:lconn.ibm.com:profiles.person:";

	public static final String MENTIONS_EVENT_CREATION_STRING = "<span class=\\\"vcard\\\"><span class=\\\"fn\\\">userName</span><span class=\\\"x-lconn-userid\\\">userId</span></span>";

	public static final String MENTIONS_MESSAGE_1 = "assearch test I am sending hello to mentioned_user";

	public static final String MENTIONS_MESSAGE_2 = "assearch test I am sending status update to mentioned_user from android";

	public static final String MENTIONS_MESSAGE_3 = "mentioned_user saying hello assearch test";

}
