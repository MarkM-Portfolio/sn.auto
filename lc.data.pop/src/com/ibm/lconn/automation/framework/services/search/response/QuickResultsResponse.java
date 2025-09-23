package com.ibm.lconn.automation.framework.services.search.response;


import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;

public class QuickResultsResponse {
	protected final static Logger logger = Logger
			.getLogger(QuickResultsResponse.class.getName());
	private String quickResultsResponseStr = "";
	private int numResultsInCurrentPage = 0;
	private int totalResults = 0;
	private int status =-1;
	
	public int getStatus() {
		return status;
	}

	private List<Page> pages;
	private static final List<String> fieldsList = Arrays.asList("contentId",
			"title", "url", "type", "source", "date", "contentCreatorId", "itemType");

	public QuickResultsResponse(ClientResponse response) {
		status = response.getStatus();
		if (status != 200) {
			logger.fine("QuickResults response status : " + status);
		}
		try {
			quickResultsResponseStr = readResponse(response.getReader(),response.toString());
			
			

		} catch (IOException e) {
			logger.fine("Can not read the response: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		if (quickResultsResponseStr != ""){
		buildJsonResponse(response.toString());
		}
	}

	private String readResponse(Reader responseReader,String responseToString) {
		if (responseReader == null) {
			logger.fine("responseReader is NULL in response: "
					+ responseToString);
			return "";
		}
		StringBuffer sb = new StringBuffer();
		try {
			int charValue = 0;
			while ((charValue = responseReader.read()) != -1) {
				sb.append((char) charValue);
			}
		} catch (IOException e) {
			logger.fine("Read response to buffer error: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}
		return sb.toString();
	}

	public void buildJsonResponse(String responseToString) {
		OrderedJSONObject jsonResponse;
		JSONArray jsonPagesArr = null;
		try {
			jsonResponse = new OrderedJSONObject(quickResultsResponseStr);
			numResultsInCurrentPage = jsonResponse
					.getInt("numResultsInCurrentPage");
			totalResults = jsonResponse.getInt("totalResults");
			jsonPagesArr = jsonResponse.getJSONArray("pages");
		} catch (JSONException e) {
			logger.fine("buildJsonResponse error: "
					+ e.getLocalizedMessage()+" response: "+ responseToString );
			e.printStackTrace();
		}
		assertTrue("jsonPagesArr is NULL in response: "+responseToString,jsonPagesArr != null);
		pages = convertJsonPagesArrToListOfPages(jsonPagesArr);
		StringBuilder sb = new StringBuilder();
		sb.append("numResultsInCurrentPage: ").append(numResultsInCurrentPage)
				.append("\n");
		sb.append("totalResults: ").append(totalResults).append("\n");
		sb.append("pages details: ").append(jsonPagesArr).append("\n");
		sb.append("pages: ").append(pages.size()).append("\n");
		logger.fine(sb.toString());
		
	}

	private List<Page> convertJsonPagesArrToListOfPages(JSONArray jsonPagesArr) {
		List<Page> pages = new ArrayList<Page>();
		for (Object object : jsonPagesArr) {
			OrderedJSONObject jsonPage = (OrderedJSONObject) object;
			Map<String, Object> pageDetails = new HashMap<String, Object>();
			for (Object field : fieldsList) {
				String fieldValue = jsonPage.get(field) != null ? jsonPage.get(field).toString() : "";
				
				pageDetails.put(field.toString(), fieldValue);
			}

			pages.add(new Page(pageDetails));

		}
		return pages;
	}

	public String getQuickResultsResponseStr() {
		return quickResultsResponseStr;
	}

	public int getNumResultsInCurrentPage() {
		return numResultsInCurrentPage;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public List<Page> getPages() {
		return pages;
	}

	public class Page {
		private Map<String, Object> pageDetails;

		Page(Map<String, Object> pageDetails) {
			this.pageDetails = pageDetails;

		}

		
		public String getId() {
			return (String) pageDetails.get("contentId");
		}

		public String getTitle() {
			return (String) pageDetails.get("title");
		}

		public String getType() {
			return (String) pageDetails.get("type");
		}

		public String getUrl() {
			return (String) pageDetails.get("url");
		}

		public String getSource() {
			return (String) pageDetails.get("source");
		}
		public String getItemType() {
			return (String) pageDetails.get("itemType");
		}
		public String getDate() {
			return (String) pageDetails.get("date");
		}

		public String getContentCreatorId() {
			return (String) pageDetails.get("contentCreatorId");
		}

		

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("Page - ");
			for (Map.Entry<String, Object> entry : pageDetails.entrySet()) {
				sb.append(entry.getKey()).append(": ").append(entry.getValue())
						.append(", ");
			}
			return sb.toString();
		}
	}

}
