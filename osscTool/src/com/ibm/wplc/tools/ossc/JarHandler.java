package com.ibm.wplc.tools.ossc;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarHandler extends VerifyFileHandler {

    public JarHandler(){
    	super();
    }

	@Override
	public void handle(FileWrapper fw) throws IOException {
		// should be guaranteed this is a jar file. just checking.
		String filename = fw.getFileOnDisk().getCanonicalPath();
        if (filename.endsWith(".jar")) {
            String hash = VerifyModulesUtil.calcHash(fw.getFileOnDisk(),getAlgorithm());
            trace("checking " + filename + " with ByHash()");
            ModuleRecord aje = getApprovedModules().findModuleByHash(hash);
            if (aje == null) {
                StringBuffer rejection = new StringBuffer();
                boolean whoDeveloped = getApprovedModules().isSelfDeveloped(new FileInputStream(filename), rejection);
                if (whoDeveloped) {
                    addSelfDeveloped(fw.getInternalName(), rejection, hash);
                } else {
                    whoDeveloped = getApprovedModules().isIBMDeveloped(new FileInputStream(filename), rejection);
                    if (whoDeveloped) {
                    	addIBMDeveloped(fw.getInternalName(), rejection, hash);
                    } else {
                        addMiss(fw.getInternalName(), rejection, hash);
                    }
                }
            }
            else {
            	addHit(fw.getInternalName(), aje);
            }
        }
	}

	public void handle(ZipEntry entry, ZipInputStream zis) throws IOException {
		// should be guaranteed this is a jar file. just checking.
		if (entry.getName().endsWith(".jar")) {
			// extract the expected size. we have seen "-1" returned
			// for valid jars, so the best we can get is an "expected"
			// size
			int expectedSize = (int)entry.getSize();
			// read the jar into a buffer so we can do additional analysis on it in case
			// it doesn't match one of our hash's
			byte ba[] = VerifyModulesUtil.readFile(zis);
			if ( expectedSize > -1 &&
					expectedSize != ba.length){
				// log that we have a mismatched file size
				trace(entry.getName() + "expected file size ("+ expectedSize
						+ ")and actual (" + ba.length + ") do not match.");
			}
			String hash = VerifyModulesUtil.calcHash(new ByteArrayInputStream(ba),getAlgorithm());
			trace("checking " + getContainer() + entry.getName() + " with byHash()");
			ModuleRecord aje = getApprovedModules().findModuleByHash(hash);
			if (aje == null) {
				StringBuffer rejection = new StringBuffer();
				boolean whoDeveloped = getApprovedModules().isSelfDeveloped(new ByteArrayInputStream(ba), rejection);
				if (whoDeveloped) {
					addSelfDeveloped(entry.getName(), rejection, hash);
				}
				else {
					whoDeveloped = getApprovedModules().isIBMDeveloped(new ByteArrayInputStream(ba), rejection);
					if (whoDeveloped) {
						addIBMDeveloped(entry.getName(), rejection, hash);
					} else {
						addMiss(entry.getName(), rejection, hash);
						rejection = new StringBuffer();
					}
				}
			} else {
				addHit(entry.getName(), aje);
			}
		}
	}
}
