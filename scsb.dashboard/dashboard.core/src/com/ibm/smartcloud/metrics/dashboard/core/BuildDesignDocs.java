package com.ibm.smartcloud.metrics.dashboard.core;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import com.ibm.json.java.*;
import org.lightcouch.*;

class BuildDesignDocs {
	private List<JSONObject> documents;
	private String timeSegments[] = new String[] {"AllTime", "Year", "Month", "Day", "Hour"};
	public BuildDesignDocs () {
		documents = new ArrayList<JSONObject>();
		for (int i=0; i<4; i++) { // Iterates through the loop, builds each for environment, appdata, and both.
			boolean environment = i%2==0;
			boolean appData = i<2;
			documents.add(this.createDoc("Get", environment, appData, "")); // Default getter
			documents.add(this.createDoc("GetOS", environment, appData, "doc.os, doc.osVer, ")); // User Agent: Operating System
			documents.add(this.createDoc("GetClient", environment, appData, "doc.client, doc.clientVer, ")); // User Agent: Client
			if (appData) {
				documents.add(this.createDoc("GetAppData", environment, true, "doc.appData, doc.appData2, ")); // Assorted application data.
			}
		}
		System.out.println(documents.toString());
	}
	public void upload (CouchDbClient uploadClient) {
		uploadClient.bulk(documents, false);
	}
	private JSONObject createDoc(String docName, boolean environment, boolean appData, String specialData) {
		JSONObject designDoc = new JSONObject();
		designDoc.put("_id", "_design/"+docName+(environment?"PerEnv":"")+(appData?"PerApp":""));
		JSONObject views = new JSONObject();
		int isNotGet = 1;
		if(docName.equals("Get")){
			isNotGet = 0;
		}
		for (int i=0; i<timeSegments.length-isNotGet; i++) {
			JSONObject view = new JSONObject();
			String viewName = "";
			String map = "function(doc){emit([";
			if (environment) {
				map += "doc.environment, ";
			}
			for (int j=0; j<i; j++) {
				map += "doc."+timeSegments[j+1].toLowerCase()+", "; // Adds in subdivisions in order, to allow appropriate grouping. AllTime should have 0 levels, whereas Month should have 2.
			}
			if (appData) {
				map += "doc.appId, ";
			}
			map += specialData;
			map += "doc.customer_id, doc.subscriber_id], null);}";
			view.put("map", map);
			view.put("reduce", "_count");
			views.put(viewName, view);
		}
		designDoc.put("views", views);
		return designDoc;
	}
	public static void main(String[] args) {
		CouchDbClient cloudantClient = new CouchDbClient("cloudant.properties");
		BuildDesignDocs build = new BuildDesignDocs();
		build.upload(cloudantClient);
	}
}