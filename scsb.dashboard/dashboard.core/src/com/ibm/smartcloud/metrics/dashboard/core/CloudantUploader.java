package com.ibm.smartcloud.metrics.dashboard.core;

import java.util.*;
import java.lang.String;
import com.ibm.json.java.*;
import org.lightcouch.*;

// Uploads an array of documents in bulk to couchDB.
// Prints counts of the errors (conflict, validation, other?)
class CloudantUploader implements Runnable {
	private float numDocs;
	private CouchDbClient cloudantClient;
	private List<JSONObject> docs;
	/**
	 * Create a bulk uploaded for a list of documents.
	 * numDocs is a float to allow easier % calculations
	 * @param List<JSONObject> documents to be uploaded
	 * @param float number of documents
	 */
	public CloudantUploader (List<JSONObject> docs, float numDocs) {
		this.numDocs = numDocs; // For percentages
		this.docs = docs;
	}
	/**
	 * Starts the bulk upload for the documents.
	 */
	public void run() {
		String dataName = Thread.currentThread().getName();
		cloudantClient = new CouchDbClient("properties/cloudant.properties");
		int errorList[] = new int[3];
		for (int numTries = 0; numTries<3; numTries++) {
			System.out.println("Starting upload number "+(numTries+1)+" for "+dataName);
			List<Response> confirm = new ArrayList<Response>();
			try {
				confirm = cloudantClient.bulk(docs, false);
				errorList = new int[3];
				if (confirm != null) {
					int confirmSize = confirm.size();
					for (int i=0; i<confirmSize; i++) {
						Response fileConfirm = confirm.get(i);
						if (fileConfirm.getError() != null) {
							if (fileConfirm.getError().equals("conflict")) {
								errorList[0]++; // Type 0 error, conflict
							} else if (fileConfirm.getError().equals("forbidden")) {
								errorList[1]++; // Type 1 error, validation
							} else {
								System.out.println(fileConfirm.getError());
								errorList[2]++; // Type 2 error, unknown
							}
						}
						if (Thread.interrupted()) {
							throw new InterruptedException();
						}
					}
				}
			} catch (InterruptedException e) {
				System.out.println(dataName+" was interrupted!");
				return;
			} catch (Exception e) {
				System.out.println("Uploading "+dataName+" has failed: "+e.getMessage());
				continue;
			}
			System.out.println("Uploading "+dataName+" has succeeded. Errors:");
			System.out.println("Conflict: "+100*errorList[0]/numDocs+"% Validation: "+100*errorList[1]/numDocs+"% Other: "+100*errorList[2]/numDocs+"%");
			System.out.println("Success: "+100*(1-(errorList[0]+errorList[1]+errorList[2])/numDocs)+"%");
			return;
		}
		System.out.println("Uploading "+dataName+" failed too many times!");
		cloudantClient.shutdown();
	}
}