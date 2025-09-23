package com.ibm.conn.auto.util;
import java.util.ArrayList;
import java.util.Map;

import org.testng.Assert;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.ibm.abilitylab.compliancechecker.ComplianceResult;
import com.ibm.abilitylab.compliancechecker.ComplianceResults;
import com.ibm.abilitylab.compliancechecker.ComplianceResult.eLevel;

public class DAPTestCaseResults {
	
	protected String _testCase;
	ArrayList<DAPScanResult> _results;
	
	public DAPTestCaseResults(String testCase){
		_testCase = testCase;
		_results = new ArrayList<DAPScanResult>();
	}
	
	public void addResult(String URL, ComplianceResults results){
		_results.add(new DAPScanResult(URL,results));
	}
	
	public String getTestCase(){
		return _testCase;
	}
	
	public void printComplianceResults(){
		System.out.println("RESULTS FOR TEST: "+_testCase+"/n");
		for(DAPScanResult scan : _results){
			System.out.println("Scan of URL: "+scan.getURL());
			for(ComplianceResult result : scan.getComplianceResults()){
				System.out.println(result.getViolationLevel()+","+result.getComponent()+","+result.getMessage()+","+result.getGid());
			}
		}
	}
	
	public void assertCompliance(){
		for(DAPScanResult scan: _results){
			assertCompliance(scan.getURL(),scan.getComplianceResults());
		}
	}
	
	private void assertCompliance(String url, ComplianceResults results){
		Map<eLevel, Integer> counts = results.summarize();
		Assert.assertTrue(counts.get(eLevel.VIOLATION).intValue() == 0, "DAP VIOLATION in test case: "+_testCase+"\n Found at: "+url+"\nThere are "+counts.get(eLevel.VIOLATION).intValue()+" violations. Assert");
	}
	
	public JsonArray createJSON() throws JsonParseException{
		System.out.println("Creating JSON");
		JsonArray scanArray = new JsonArray();
		for(DAPScanResult s : _results){
			JsonObject scan = new JsonObject();
			JsonArray resultArray = new JsonArray();
			for(ComplianceResult r : s.getComplianceResults()){
				System.out.println("ADDING NEW RESULT ");
				JsonObject result = new JsonObject();
				
				result.add("violation level", new JsonPrimitive(r.getViolationLevel().toString()));
				result.add("message", new JsonPrimitive(r.getMessage()));
				result.add("component", new JsonPrimitive(r.getComponent()));
				resultArray.add(result);
			}
			System.out.println(scan.toString());
			scan.add(s.getURL(), resultArray);
			scanArray.add(scan);
		}
		System.out.println(scanArray.toString());
		System.out.println("FINISHED PRINTING SCAN ARRAY");
		return scanArray;
	}
	
	public String createHTML(){
		String scanList = "";
		for(DAPScanResult s : _results){
			String scan = "";
			String resultList = "";
			for(ComplianceResult r : s.getComplianceResults()){
				String result = "<p>" + r.getViolationLevel() + ", " + r.getMessage() + ", " + r.getComponent() + "</p>";
				resultList += result;
			}
			scan = "<h3>Scan of URL: " + s.getURL() + "</h3>" + resultList;
			scanList += scan;
		}
		return scanList;
	}
	
	
	
	

}
