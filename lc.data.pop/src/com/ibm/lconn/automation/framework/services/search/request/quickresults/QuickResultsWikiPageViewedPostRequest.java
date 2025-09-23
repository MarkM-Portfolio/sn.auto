
package com.ibm.lconn.automation.framework.services.search.request.quickresults;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;



public class QuickResultsWikiPageViewedPostRequest extends QuickResultsWithContainerPostRequest{


	public final static String QUICK_RESULTS_SOURCE_WIKI = "WIKIS";
	public final static String QUICK_RESULTS_ITYPE_WIKI = "PAGE";

	/* 
	 * Request Params Example:
	 * 
	 * contentId:24d166f9-cc6a-4c71-bc24-6a00e64a68cd
	 * itemType:PAGE
	 * source:WIKIS
	 * i:1
	 * contentLink:/ic4/wikis/home/wiki/Quick%20Results%20Noga%20Testing%20Wiki/page/Welcome%20to%20Quick%20Results%20Noga%20Testing%20Wiki?lang=en-us
	 * contentTitle:Welcome to Quick Results Noga Testing Wiki
	 * contentContainerId:b41db256-1f9b-4cc2-b3de-30bd716f55fe
	 * contentContainerTitle:Quick Results Noga Testing Wiki
	 * contentContainerLink:/ic4/wikis/home/wiki/Quick%20Results%20Noga%20Testing%20Wiki?lang=en-us
	 * contentCreatorId:9a20f540-06b4-102c-8335-b3e03759c05f
	 * contentCreateTs:Wed Sep 17 2014 11:56:46 GMT+0300 (Jerusalem Daylight Time)

	 */
	

	
	public QuickResultsWikiPageViewedPostRequest(String wikiId,
			String wikiTitle, String wikiCreatorId, String wikiCreateTs,
			String wikiLink, String wikiContainerId, String wikiContainerTitle,
			String wikiContainerLink) {
		super(wikiId, QUICK_RESULTS_SOURCE_WIKI, wikiTitle, wikiCreatorId, wikiCreateTs,
				wikiLink, QUICK_RESULTS_ITYPE_WIKI, wikiContainerId, wikiContainerTitle,
				wikiContainerLink);
	}

	@Override
	protected String getContentPath() {
		String encodedContainerTitle = "";
		try {
			encodedContainerTitle = URLEncoder.encode(contentContainerTitle, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		String encodedContentTitle = "";
		try {
			encodedContentTitle = URLEncoder.encode(contentTitle, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return "/wikis/home/wiki/"+encodedContainerTitle+"/page/"+encodedContentTitle+"?lang=en-us";
	}

	@Override
	public String toString() {
		return "QuickResultsWikiPageViewedPostRequest [toString()="
				+ super.toString() + "]";
	}
	
	

}
