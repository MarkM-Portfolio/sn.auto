package com.ibm.conn.auto.util;

import java.util.ArrayList;
import java.util.List;

public class DefectLogger {
	private List<String> steps = new ArrayList<String>();
	private String weakStep = null;
	
	/**
	 * Step that will show up in the defect
	 * @param step
	 */
	public void strongStep(String step) {
		steps.add(step);
		weakStep = null;
	}
	
	/**
	 * Step that will be overwritten by next weak step unless it is locked
	 * @param step
	 */
	public void weakStep(String step) {
		weakStep = step;
	}
	
	/**
	 * Makes last weak step a strong step
	 */
	public void lockStep() {
		steps.add(weakStep);
	}
	
	public String print() {
		String result = "";
		int count = steps.size();
		for(int i = 0; i < count; i++) {
			result = result + (i + 1) + ". " + steps.get(i) + "\r\n";
		}
		if (weakStep != null) {
			result = result + count + ". " + weakStep;
		}
		return result;
	}
}
