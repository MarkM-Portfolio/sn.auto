
package com.ibm.lconn.automation.framework.services.search.request.quickresults;



public class QuickResultsFileViewedPostRequest extends QuickResultsWithContainerPostRequest{


	public final static String QUICK_RESULTS_SOURCE_FILE = "FILES";
	public final static String QUICK_RESULTS_ITYPE_FILE = "FILE";
	

	/* 
	 * Request Params Example:
	 * 
	 * contentId:f16c05a0-3f60-4a87-841f-39fca079235b
	 * itemType:FILESUMMARY
	 * source:FILES
	 * i:2
	 * contentTitle:131833.png
	 * contentContainerId:fe3a6135-a2ac-43c2-a351-a56d436cd88c
	 * contentContainerTitle:Noga Tor
	 * contentCreatorId:9a20f540-06b4-102c-8335-b3e03759c05f
	 * contentCreateTs:Wed Oct 22 2014 14:14:05 GMT+0300 (Jerusalem Daylight Time)
	 * contentLink:/ic4/files/app/file/f16c05a0-3f60-4a87-841f-39fca079235b
	*/

	
	
	public QuickResultsFileViewedPostRequest(String fileId, String fileTitle,
			String fileCreatorId, String fileCreateTs,
			String fileLink, String fileContainerId, String fileContainerTitle,
			String fileContainerLink) {
		super(fileId, QUICK_RESULTS_SOURCE_FILE, fileTitle, fileCreatorId, fileCreateTs,
				fileLink, QUICK_RESULTS_ITYPE_FILE, fileContainerId, fileContainerTitle,
				fileContainerLink);
	}
	@Override
	protected String getContentPath() {
		return "/files/app/file/"+contentId;
	}
	



	@Override
	public String toString() {
		return "QuickResultsStandaloneFileViewedPostRequest [toString()="
				+ super.toString() + "]";
	}

	
}
