package com.ibm.wplc.tools.ossc;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExplicitJsHandler extends VerifyFileHandler {

     public ExplicitJsHandler(){
    	super();
   	 System.out.println("!!! USING EXPLICITJSHANLDER !!!");
    }

	public void handle(FileWrapper fw) throws IOException {
		String filename = fw.getFileOnDisk().getCanonicalPath();
		if ( filename.endsWith(".js")){
			String hash = VerifyModulesUtil.calcHash(fw.getFileOnDisk(),getAlgorithm());
			trace("checking " + fw.getInternalName() + " with byHash()");
			ModuleRecord aje = getApprovedModules().findModuleByHash(hash);
			if (aje == null) {
				addMiss(fw.getInternalName(), null, hash);
			} else {
				addHit(fw.getInternalName(), aje);
			}
		}
	}

	public void handle(ZipEntry entry, ZipInputStream zis) throws IOException {
		String filename = entry.getName();
		if (filename.endsWith(".js")) {
			String hash = VerifyModulesUtil.calcHash(zis,getAlgorithm());
			trace("checking " + getContainer() + entry.getName() + " with byHash()");
			ModuleRecord aje = getApprovedModules().findModuleByHash(hash);
			if (aje == null) {
				addMiss(entry.getName(), null, hash);
			} else {
				addHit(entry.getName(), aje);
			}
		}
	}
}
