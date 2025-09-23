/**
 * 
 */
package com.ibm.lconn.automation.framework.search.rest.api;

import static org.testng.AssertJUnit.fail;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.lang.StringUtils;

import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.search.data.Application;

/**
 * @author reuven
 * 
 */
public class SearchRestAPIUtils {

	// private static String _execId = null;
	private static Random randomGenerator = new Random();

	private static Hashtable<Purpose, String> executionIDs = new Hashtable<Purpose, String>();

	public static enum Purpose {
		SEARCH, PEOPLE_FINDER, TEMP, UNIFY, QUICK_RESULTS, SEARCH_SOLR_ENGINE,SEARCH_SCOPE, JAPANESE, FACETING, RECOMMENDATIONS, ORIENTME
	};

	public static String getExecId(Purpose purpose) {
		String executionID = executionIDs.get(purpose);
		if (executionID == null) {
			executionID = Long.toString(System.currentTimeMillis());
			executionIDs.put(purpose, executionID);
		}
		return executionID;
	}

	/**
	 * Convert the updated value of atom entry to Date
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date getFormattedDate(String dateStr) {
		DateFormat formatter1;
		String mask = "yyyy-MM-dd'T'HH:mm:ss";
		formatter1 = new SimpleDateFormat(mask);
		Date updatedDate = null;
		try {
			updatedDate = (Date) formatter1.parse(dateStr);
		} catch (ParseException e) {
			fail(e.getLocalizedMessage());
		}
		return updatedDate;
	}

	public static String highlight(String title, String query) {
		int queryStartIndex = title.indexOf(query);
		if (queryStartIndex == -1) {
			fail();
		}
		return title.substring(0, queryStartIndex)
				+ " <b>"
				+ query
				+ "</b> "
				+ title.substring(queryStartIndex + query.length(),
						title.length());
	}

	public static String removeHighlighting(String title) {
		if (title == null) {
			return null;
		}
		title = title.replaceAll("<b>", "");
		title = title.replace("</b>", "");
		return title;
	}

	/**
	 * Generates title for <code>Purpose.SEARCH</code>
	 */
	public static String generateTitle(Permissions permission,
			Application appGroup) {
		return generateTitle(permission, appGroup, Purpose.SEARCH);
	}

	public static String generateTitle(Permissions permission,
			Application appGroup, Purpose purpose) {
		StringBuilder title = new StringBuilder(getExecId(purpose));
		int randomWordPlace = randomGenerator
				.nextInt(SearchRestApiProperties.titleWord.length);
		title.append(" ").append(
				SearchRestApiProperties.titleWord[randomWordPlace]);
		title.append(" ").append(permission);
		title.append(" ").append(appGroup.toString());
		return title.toString();
	}
	
	public static String generateTitleFromTemplateString(String templateString,Purpose purpose) {
		StringBuilder title = new StringBuilder(getExecId(purpose));
		
				
		title.append(" ").append(templateString);
		
		return title.toString();
	}
	
	public static String generateTagValue(Purpose purpose) {
		return StringUtils.reverse(getExecId(purpose));

	}

	public static String contentForSearchExtracted(Purpose purpose) {
		return StringUtils.reverse(getExecId(purpose)) + "a"
				+ getExecId(purpose);

	}

	public static String generateDescription(String title) {
		return StringUtils.reverse(title)+"description";

	}
	
	public static String addExecIdToTemplate(String template, Purpose purpose) {
		StringBuilder description = new StringBuilder(getExecId(purpose));
		description.append(" ").append(template);
		return description.toString();
	}

	public static void addCredentials(AbderaClient client,
			UsernamePasswordCredentials credentials, ServiceEntry serviceEntry)
			throws URISyntaxException {
		boolean _useSSL = true;
		Component componet = serviceEntry.getComponent();
		String realmApp = null;
		if (componet.equals(Component.COMMUNITIES)) {
			realmApp = StringConstants.AUTH_REALM_COMMUNITIES;
		} else if (componet.equals(Component.ACTIVITIES)) {
			realmApp = StringConstants.AUTH_REALM_ACTIVITIES;
		} else if (componet.equals(Component.BLOGS)) {
			realmApp = StringConstants.AUTH_REALM_BLOGS;
		} else if (componet.equals(Component.CRE)) {
			realmApp = StringConstants.AUTH_REALM_CRE;
		} else if (componet.equals(Component.DOGEAR)) {
			realmApp = StringConstants.AUTH_REALM_DOGEAR;
		} else if (componet.equals(Component.FILES)) {
			realmApp = StringConstants.AUTH_REALM_FILES;
		} else if (componet.equals(Component.FORUMS)) {
			realmApp = StringConstants.AUTH_REALM_FORUMS;
		} else if (componet.equals(Component.WIKIS)) {
			realmApp = StringConstants.AUTH_REALM_WIKIS;
		} else if (componet.equals(Component.PROFILES)) {
			realmApp = StringConstants.AUTH_REALM_PROFILES;
		}

		if (_useSSL) {
			client.addCredentials(serviceEntry.getSslHrefString(), realmApp,
					StringConstants.AUTH_BASIC, credentials);
			client.addCredentials(serviceEntry.getSslHrefString(),
					StringConstants.AUTH_REALM_FORCED,
					StringConstants.AUTH_BASIC, credentials);
		} else {
			client.addCredentials(serviceEntry.getHrefString(), realmApp,
					StringConstants.AUTH_BASIC, credentials);
			client.addCredentials(serviceEntry.getHrefString(),
					StringConstants.AUTH_REALM_FORCED,
					StringConstants.AUTH_BASIC, credentials);
		}
	}

	public static String getEmailDomain(String email) {
		String domain = email.substring(email.indexOf("@") + 1);
		return domain;
	}
}
