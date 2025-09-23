package com.ibm.atmn.waffle.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ibm.atmn.waffle.core.RetryAnalyzer;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public class BaseSuiteListener implements ISuiteListener{
	private static long startTime;

	@Override
	public void onFinish(ISuite suite) {
		long duration = System.currentTimeMillis() - startTime;
		suite.setAttribute("duration", String.format( "%.3f", duration/60000.0 ));
		for(String suiteName: suite.getResults().keySet()) {
			for(ITestResult result: suite.getResults().get(suiteName).getTestContext().getPassedTests().getAllResults()) {
				result.setAttribute("retriesNum", RetryAnalyzer.retryCount.get(result.getInstanceName()+result.getMethod()));
			}
		}
		
	}

	@Override
	public void onStart(ISuite suite) {
		startTime = System.currentTimeMillis();
		
		if (suite.getName().equalsIgnoreCase("printStructure")) {
			try {
				JsonObject jsonOutput = new JsonObject();
				for(ITestNGMethod m: suite.getAllMethods()) {
					JsonObject test = new JsonObject();
				
					test.addProperty("class", m.getTestClass().getName());
					test.addProperty("name", m.getMethodName());
					JsonArray ja = new JsonArray();
					for(String g : m.getGroups()) {
						ja.add(new JsonPrimitive(g));
					}
					test.add("groups", ja);
					String component = m.getXmlTest().getName();
					
					JsonArray compArray = jsonOutput.getAsJsonArray(component);
					if(compArray == null)
						compArray = new JsonArray();
					compArray.add(test);
					jsonOutput.add(component, compArray);
				}
				System.out.println(jsonOutput.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

}
