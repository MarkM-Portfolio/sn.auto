package com.ibm.wplc.tools.ossc;

import java.io.File;

public class FileWrapper {
	private String internalName;
	private File   fileOnDisk;

	public FileWrapper(String internalName, File fileOnDisk){
		this.internalName = internalName;
		this.fileOnDisk   = fileOnDisk;
	}

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	public File getFileOnDisk() {
		return fileOnDisk;
	}

	public void setFileOnDisk(File fileOnDisk) {
		this.fileOnDisk = fileOnDisk;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("internal name : " + internalName);
		sb.append(" file on disk: " + fileOnDisk.getAbsolutePath());
		return sb.toString();
	}
}
