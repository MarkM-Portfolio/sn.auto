package com.ibm.wplc.tools.ossc;

import java.util.HashSet;
import java.util.Iterator;

public class IgnoreRecord {

    private String targetName;               // the target file on the ignore list
    private HashSet<String> containerNames; // a list of containers this target may be in

    public IgnoreRecord(String targetName){
    	this.targetName = targetName;
    	containerNames = new HashSet<String>(5); // guess 5 here
    }

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public void addContainerName(String name) {
		containerNames.add(name);
	}

	public Iterator<String> getContainerNames(){
		return containerNames.iterator();
	}
}
