package com.ibm.atmn.waffle.base;

import java.util.Properties;
import java.util.Set;

import org.testng.IExecutionListener;

import com.ibm.atmn.waffle.core.RunConfiguration;

public class BaseExecutionListener implements IExecutionListener{
	
	@Override
	public void onExecutionFinish() {
		
	}

	@Override
	public void onExecutionStart() {
		
		Properties props = RunConfiguration.getSystemProperties();
		Set<String> set = props.stringPropertyNames();
		
		for(String prop : set){
			
			System.setProperty(prop, props.getProperty(prop));
		}
		
	}

}
