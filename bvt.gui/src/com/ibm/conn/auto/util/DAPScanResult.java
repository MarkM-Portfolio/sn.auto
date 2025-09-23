package com.ibm.conn.auto.util;

import com.ibm.abilitylab.compliancechecker.ComplianceResults;

public class DAPScanResult {

	private String _url;
	private ComplianceResults  _results;
	
	public DAPScanResult(String url, ComplianceResults results){
		_url = url;
		_results = results;
	}
	
	public String getURL(){
		return _url;
	}
	
	public ComplianceResults getComplianceResults(){
		return _results;
	}
}
