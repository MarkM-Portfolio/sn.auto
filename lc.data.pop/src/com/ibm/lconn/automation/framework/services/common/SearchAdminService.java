package com.ibm.lconn.automation.framework.services.common;

import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;

import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.IndexNowOnCloudType;
import com.ibm.lconn.automation.framework.services.search.service.SearchService;

public class SearchAdminService extends LCService {
	private static Abdera abdera = new Abdera();
	private static AbderaClient searchAdminClient = new AbderaClient(abdera);
	private static ServiceEntry searchEntry;
	private static ServiceConfig config;
	private final static String INDEX_STATUS_COMPLETED = "INDEX_COMPLETED";
	private final static String INDEX_STATUS_DB_EMPTY = "DB_EMPTY";
	private final static String INDEX_STATUS_UNKNOWN = "UNKNOWN";
	final private String INDEX_NOW_REQUEST = URLConstants.SERVER_URL
			+ "/search/searchAdmin?command=indexNow&params=";
	final private String EXTRACT_NOW_REQUEST = URLConstants.SERVER_URL
			+ "/search/searchAdmin?command=getFileContentNow&params=";
	final private String FILE_CONTENT_INDEX_NOW_REQUEST = URLConstants.SERVER_URL
			+ "/search/searchAdmin?command=indexFileContentNow&params=";
	final private String SAND_INDEX_NOW_REQUEST = URLConstants.SERVER_URL
			+ "/search/searchAdmin?command=sandIndexNow&params=";
	final private String GET_INDEX_STATUS = URLConstants.SERVER_URL
			+ "/search/searchAdmin?command=getIndexStatus&params=";
	final private int MILLISEC_IN_MINUTE = 60000;
	private int TIMEOUT_MINUTES;
	private int TIMEOUT_MINUTES_FOR_SVT = 30;

	public enum IndexNowStatus {
		RUNNING, COMPLETED, UNKNOWN, FAILED
	};

	public enum FileExtractStatus {
		COMPLETED, FAILED
	};
	public enum FileContentIndexStatus {
		COMPLETED, FAILED
	};
	// final private String LIST_TASKS_REQUEST = URLConstants.SERVER_URL +
	// "/search/searchAdmin?command=listTasks";
	static {

		try {
			config = new ServiceConfig(searchAdminClient, URLConstants.SERVER_URL,
					true);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(e.getMessage(), false);

		}
		searchEntry = config.getService("search");

	}

	public SearchAdminService() {
		super(searchAdminClient, searchEntry);
	}

	public void sandIndexNow() throws UnsupportedEncodingException {
		String components = "manageremployees,tags,taggedby,communitymembership,evidence,graph";
		sandIndexNow(components);
	}

	public void sandIndexNow(String component)
			throws UnsupportedEncodingException {
		sandIndexNow(component, null, null);
	}

	public void sandIndexNow(String component, String adminUserName,
			String adminPassword) throws UnsupportedEncodingException {
		getApiLogger().debug(SearchService.class.getName());
		getApiLogger().debug("Wait for SaND indexing...");
		// login as
		if (adminUserName == null || adminPassword == null)
			loginToSearchAdminService();
		else
			loginToSearchAdminService(adminUserName, adminPassword);
		TIMEOUT_MINUTES = 2;
		long timeOut = TIMEOUT_MINUTES * MILLISEC_IN_MINUTE; // 2 min
		int attempts = 0;
		long sleepTime = 2 * 1000; // 2 sec * 1000 mlsec

		String indexNowRequest = SAND_INDEX_NOW_REQUEST
				+ URLEncoder.encode("[\"" + component + "\"]", "UTF-8");
		JSONArray indexingTaskName = sendSearchAdminCommand(indexNowRequest);

		if (indexingTaskName == null || indexingTaskName.size() != 1) {
			assertTrue("sandIndexNow task has no results", false);
			return;
		}

		while (true) {

			try {
				long time = attempts * sleepTime;
				if (time <= timeOut) {
					attempts++;
					getApiLogger().debug(
							"SaND indexing.... " + time / 1000 + " / "
									+ timeOut / 1000 + " sec.");
					Thread.sleep(sleepTime);
				} else {
					getApiLogger().debug("The SaND" + component + " indexed.");
					break;
				}
			} catch (InterruptedException e) {
				getApiLogger().debug(e.getMessage());
			}
		}
	}

	public IndexNowStatus indexNow() throws UnsupportedEncodingException {
		String ALL_COMPONENTS = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			ALL_COMPONENTS = "activities,communities,files,status_updates,people_finder";
		} else {
			ALL_COMPONENTS = "all_configured";
		}

		return indexNow(ALL_COMPONENTS);

	}

	public FileExtractStatus fileExtractNow()
			throws UnsupportedEncodingException {
		String components = "all_configured";
		return fileExtractNow(components);

	}

	public FileExtractStatus fileExtractNow(String components)
			throws UnsupportedEncodingException {

		return fileExtractNow(null, null, components);

	}

	public IndexNowStatus indexNow(String component)
			throws UnsupportedEncodingException {

		return indexNow(component, null, null);

	}

	public void indexNowActivitiesOnCloud() throws Exception {

		if (StringConstants.INDEX_NOW_ON_CLOUD_TYPE == IndexNowOnCloudType.WSADMIN) {
			sendIndexNowBywsadmin("activities");
			// 3 min waiting for indexing completed
			TIMEOUT_MINUTES = 3;
			long timeOut = TIMEOUT_MINUTES * MILLISEC_IN_MINUTE;
			try {
				Thread.sleep(timeOut);
			} catch (InterruptedException e) {
				getApiLogger().debug(e.getMessage());
			}
			return;
		}
		if (URLConstants.SERVER_URL.contains("svt")){
			getApiLogger().debug("Wait for indexing 30 min");
			TIMEOUT_MINUTES = TIMEOUT_MINUTES_FOR_SVT;
		}else {
		getApiLogger().debug("Wait for indexing 15 min");
		TIMEOUT_MINUTES = 15;
		}
		long timeOut = TIMEOUT_MINUTES * MILLISEC_IN_MINUTE;
		try {
			Thread.sleep(timeOut);
		} catch (InterruptedException e) {
			getApiLogger().debug(e.getMessage());
		}

	}

	public void indexNowOnCloud()  {

		if (StringConstants.INDEX_NOW_ON_CLOUD_TYPE == IndexNowOnCloudType.WSADMIN) {
			try {
				sendIndexNowBywsadmin("activities");
				sendIndexNowBywsadmin("files");
				sendIndexNowBywsadmin("communities");
			} catch (Exception e) {
				getApiLogger().debug(e.getMessage());
			}
			

			// 3 min waiting for indexing completed
			TIMEOUT_MINUTES = 3;
			long timeOut = TIMEOUT_MINUTES * MILLISEC_IN_MINUTE;
			try {
				Thread.sleep(timeOut);
			} catch (InterruptedException e) {
				getApiLogger().debug(e.getMessage());
			}
			return;
		}

		if (URLConstants.SERVER_URL.contains("svt")){
			getApiLogger().debug("Wait for indexing 30 min");
			TIMEOUT_MINUTES = TIMEOUT_MINUTES_FOR_SVT;
		}else {
		getApiLogger().debug("Wait for indexing 15 min");
		TIMEOUT_MINUTES = 15;
		}
		long timeOut = TIMEOUT_MINUTES * MILLISEC_IN_MINUTE;
		try {
			Thread.sleep(timeOut);
		} catch (InterruptedException e) {
			getApiLogger().debug(e.getMessage());
		}

	}

	public IndexNowStatus indexNow(String component, String adminUserName,
			String adminPassword) throws UnsupportedEncodingException {
		getApiLogger().debug(SearchService.class.getName());
		getApiLogger().debug("Wait for indexing...");
		// login as
		if (adminUserName == null || adminPassword == null) {
			loginToSearchAdminService();
		} else {
			loginToSearchAdminService(adminUserName, adminPassword);
		}
		TIMEOUT_MINUTES = 10;
		long timeOut = TIMEOUT_MINUTES * MILLISEC_IN_MINUTE; // 20 min * 60 sec
																// * 1000 mlsec
		int attempts = 0;
		long sleepTime = 2 * 1000; // 2 sec * 1000 mlsec

		String indexNowRequest = INDEX_NOW_REQUEST
				+ URLEncoder.encode("[\"" + component + "\"]", "UTF-8");
		JSONArray indexingTaskName = sendSearchAdminCommand(indexNowRequest);

		if (indexingTaskName == null || indexingTaskName.size() != 1) {
			assertTrue("IndexNow task has no results", false);
			return IndexNowStatus.FAILED;
		}
		String indexingTaskNameStr = (String) indexingTaskName.get(0);

		JSONArray listOfRunningTaskIds = null;
		while (true) {
			assertTrue("Wait for indexing new data is timed out ", sleepTime
					* attempts < timeOut);
			String getIndexStatusRequest = GET_INDEX_STATUS
					+ URLEncoder.encode("[\"" + indexingTaskNameStr + "\"]",
							"UTF-8");
			listOfRunningTaskIds = sendSearchAdminCommand(getIndexStatusRequest);
			if (listOfRunningTaskIds == null) {
				assertTrue("ListTasks has no results", false);
				return IndexNowStatus.FAILED;
			}
			try {
				IndexNowStatus status = isIndexingFinished(listOfRunningTaskIds);
				if (status == IndexNowStatus.RUNNING) {
					attempts++;
					getApiLogger().debug(
							"Attempt# " + attempts + ", " + sleepTime
									* attempts / 1000 + " / " + timeOut / 1000
									+ " sec.");
					
					Thread.sleep(sleepTime);
				} else {

					return status;

				}
			} catch (InterruptedException e) {
				getApiLogger().debug(e.getMessage());
			}
		}

	}

	public FileExtractStatus fileExtractNow(String adminUserName,
			String adminPassword, String components)
			throws UnsupportedEncodingException {

		getApiLogger().debug(SearchService.class.getName());
		getApiLogger().debug("Wait for extraction...");
		// login as
		if (adminUserName == null || adminPassword == null) {
			loginToSearchAdminService();
		} else {
			loginToSearchAdminService(adminUserName, adminPassword);
		}
		TIMEOUT_MINUTES = 2;
		long timeOut = TIMEOUT_MINUTES * MILLISEC_IN_MINUTE;
		int attempts = 0;
		long sleepTime = 2 * 1000; // 2 sec * 1000 mlsec

		String extractNowRequest = EXTRACT_NOW_REQUEST
				+ URLEncoder.encode("[\"" + components + "\"]", "UTF-8");
		;
		JSONArray indexingTaskName = sendSearchAdminCommand(extractNowRequest);

		if (indexingTaskName == null || indexingTaskName.size() != 1) {
			assertTrue("ExtractNow task has no results", false);
			return FileExtractStatus.FAILED;
		} else {
			String indexingTaskNameStr = (String) indexingTaskName.get(0);

			while (true) {

				try {
					long time = attempts * sleepTime;
					if (time <= timeOut) {
						attempts++;
						getApiLogger().debug(
								"File extraction task " + indexingTaskNameStr
										+ " running..." + time / 1000 + " / "
										+ timeOut / 1000 + " sec.");
						Thread.sleep(sleepTime);
					} else {
						getApiLogger().debug(
								"The File extraction task "
										+ indexingTaskNameStr + " completed.");
						break;
					}
				} catch (InterruptedException e) {
					getApiLogger().debug(e.getMessage());
				}
			}
			return FileExtractStatus.COMPLETED;
		}
	}
	
	public FileContentIndexStatus fileContentIndexNow()
			throws UnsupportedEncodingException {

		return fileContentIndexNow(null, null);

	}
	public FileContentIndexStatus fileContentIndexNow(String adminUserName,
			String adminPassword)
			throws UnsupportedEncodingException {

		getApiLogger().debug(SearchService.class.getName());
		getApiLogger().debug("Wait for Files content indexing...");
		// login as
		if (adminUserName == null || adminPassword == null) {
			loginToSearchAdminService();
		} else {
			loginToSearchAdminService(adminUserName, adminPassword);
		}
		TIMEOUT_MINUTES = 5;
		long timeOut = TIMEOUT_MINUTES * MILLISEC_IN_MINUTE;
		int attempts = 0;
		long sleepTime = 2 * 1000; // 2 sec * 1000 mlsec
		String components = "all_configured";
		String fileContentIndexNowRequest = FILE_CONTENT_INDEX_NOW_REQUEST
				+ URLEncoder.encode("[\"" + components + "\",\"300\"]", "UTF-8");
		
		JSONArray indexingTaskName = sendSearchAdminCommand(fileContentIndexNowRequest);

		if (indexingTaskName == null || indexingTaskName.size() != 1) {
			assertTrue("fileContentIndexNow task has no results", false);
			return FileContentIndexStatus.FAILED;
		} else {
			String indexingTaskNameStr = (String) indexingTaskName.get(0);

			while (true) {

				try {
					long time = attempts * sleepTime;
					if (time <= timeOut) {
						attempts++;
						getApiLogger().debug(
								"File ContentIndexNow " + indexingTaskNameStr
										+ " running..." + time / 1000 + " / "
										+ timeOut / 1000 + " sec.");
						Thread.sleep(sleepTime);
					} else {
						getApiLogger().debug(
								"The File ContentIndexNow task "
										+ indexingTaskNameStr + " completed.");
						break;
					}
				} catch (InterruptedException e) {
					getApiLogger().debug(e.getMessage());
				}
			}
			return FileContentIndexStatus.COMPLETED;
		}
	}

	private IndexNowStatus isIndexingFinished(JSONArray result) {

		getApiLogger().debug("isIndexingFinished Result: " + result);
		String indexStatus = (String) result.get(0);

		if (indexStatus.equals(INDEX_STATUS_COMPLETED)) {
			return IndexNowStatus.COMPLETED;
		} else if (indexStatus.equals(INDEX_STATUS_UNKNOWN)) {
			getApiLogger().debug("The index status is unknown");
			return IndexNowStatus.UNKNOWN;
		} else if (indexStatus.equals(INDEX_STATUS_DB_EMPTY)) {
			getApiLogger().debug("The index status reported that DB is empty");
			return IndexNowStatus.UNKNOWN;
		}
		return IndexNowStatus.RUNNING;
	}

	private JSONArray sendSearchAdminCommand(String requestUrl) {

		getApiLogger().debug("sendSearchAdminCommand");
		try {
			ClientResponse response = doSearch(requestUrl);
			int status = response.getStatus();
			if (status != 200) {
				getApiLogger().debug(
						"assertTrues to execute Admin command - status : " + status);
				assertTrue("assertTrues to execute - status : " + status, false);
				return null;
			}
			String responseStr;

			responseStr = readResponse(response.getReader());
			getApiLogger().debug(responseStr);
			OrderedJSONObject jsonResponse = new OrderedJSONObject(responseStr);
			boolean success = (boolean) jsonResponse.getBoolean("success");
			if (!success) {
				getApiLogger().debug(
						"assertTrues to execute Admin command - success : " + success);
				assertTrue("assertTrues to excecute " + requestUrl, false);
				return null;
			}
			return jsonResponse.getJSONArray("result");
		} catch (IOException e) {
			getApiLogger().debug(e.getMessage());
		} catch (JSONException e) {
			getApiLogger().debug(e.getMessage());
		}
		return null;

	}

	public ClientResponse doSearch(String url) {
		return doSearch(searchAdminClient, url);
	}

	private void loginToSearchAdminService() {
		loginToSearchAdminService(StringConstants.ADMIN_USER_NAME,
				StringConstants.ADMIN_USER_PASSWORD);
	}

	private void sendIndexNowBywsadmin(String component) throws Exception {
		String command;
		ArrayList<String> test1;
		if (component == "activities") {
			command = "cmd /C CHDIR /D C:\\MyThinclient\\ && cmd /C echo y | wsadmin.bat -lang jython -f C:\\py\\IndexActivities.py";
		} else if (component == "communities") {
			command = "cmd /C CHDIR /D C:\\MyThinclient\\ && cmd /C echo y | wsadmin.bat -lang jython -f C:\\py\\IndexerCommunities.py";
		} else if (component == "files") {
			command = "cmd /C CHDIR /D C:\\MyThinclient\\ && cmd /C echo y | wsadmin.bat -lang jython -f C:\\py\\IndexerFiles.py";
		} else {
			getApiLogger().debug(
					"The indexing script may have FAILED. " + component);
			throw new Exception("wsadmin Indexing FAILED");
		}
		test1 = execIndex(command);

		getApiLogger().debug("wsadmin IndexNow " + component + ": " + test1);
		if (test1.contains("assertTrues") || test1.contains("Error")) {
			getApiLogger().debug(
					"The indexing script may have FAILED. " + component);
			throw new Exception("wsadmin Indexing FAILED");
		}
	}

	private ArrayList<String> execIndex(String somecommand) {
		ArrayList<String> result = new ArrayList<String>();

		try {
			Process proc = Runtime.getRuntime().exec(somecommand);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));

			String line = null;
			while ((line = in.readLine()) != null) {
				getApiLogger().debug(line);
				result.add(line);
				if (line.contains("Error")) {
					result.add("Error");
				}
			}

		} catch (Exception e) {

			getApiLogger().debug(e.getMessage());
		}
		return result;
	}

	private void loginToSearchAdminService(String username, String password) {
		getApiLogger().debug("loginToSearchAdminService start");
		// login with admin user
		HttpClient http = new HttpClient();
		http.getState().clearCookies();

		Cookie[] cookies;

		String auth = StringConstants.AUTHENTICATION;
		if (auth.equalsIgnoreCase(StringConstants.Authentication.BASIC
						.toString()) || auth.equalsIgnoreCase(StringConstants.Authentication.FORM
								.toString())) {
			cookies = executeJLogin(http, username, password);
		} else {
			cookies = executeLogin(http, username, password);
		}

		for (Cookie cookie : cookies) {
			searchAdminClient.addCookies(cookie);
		}
		getApiLogger().debug("loginToSearchAdminService end");
	}

}
