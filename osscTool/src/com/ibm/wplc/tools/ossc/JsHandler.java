package com.ibm.wplc.tools.ossc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JsHandler extends VerifyFileHandler {

    private final Pattern _dojoBuildPattern = Pattern.compile("\\\"\\$Rev: ([0-9]+)\\ \\$");

    public JsHandler(){
    	super();
    }

	public void handle(FileWrapper fw) throws IOException {
		// should be guaranteed this is a jar file. just checking.
		String filename = fw.getFileOnDisk().getCanonicalPath();
		if ( filename.endsWith(".js")){
			if (isDojoFile(filename) == true) {
				StringBuffer rejection = new StringBuffer();
				FileInputStream fis = new FileInputStream(filename);
				trace("checking " + filename + " with verifyDojo()");
				ModuleRecord aje = verifyDojo(fis, rejection);
				if (aje == null) {
					addMiss(fw.getInternalName(), rejection, "<na>");
				}
				else {
					addHit(fw.getInternalName(), aje);
				}
			} else if (getApprovedModules().jsNameMatch(fw.getInternalName())) {
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
	}

	public void handle(ZipEntry entry, ZipInputStream zis) throws IOException {
		// should be guaranteed this is a jar file. just checking.
		String filename = entry.getName();
		if (filename.endsWith(".js")) {
			if (isDojoFile(entry.getName())) {
					StringBuffer rejection = new StringBuffer();
					trace("checking " + getContainer() + entry.getName() + " with verifyDojo()");
					ModuleRecord aje = verifyDojo(zis, rejection);
					if (aje == null) {
						addMiss(entry.getName(), rejection, "<na>");
						rejection = new StringBuffer();
					} else {
						addHit(entry.getName(), aje);
					}
			}
			else if ( getApprovedModules().jsNameMatch(entry.getName())) {
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

	private boolean isDojoFile(String path) {
		if (path.endsWith("/dojo.js") || path.endsWith("\\dojo.js")) {
			return true;
		}
		return false;
	}
	/**
	 * Given an input stream to a dojo file, search for the dojo version and try to match
	 * that against
	 * 
	 * @param is
	 * @param rejection
	 * @return
	 * @throws IOException
	 */
	public ModuleRecord verifyDojo(InputStream is, StringBuffer rejection) throws IOException {
		byte bytes[] = new byte[5000];
		int i=0, total=0;
		while ((i > -1 && total < bytes.length)) {
			i = is.read(bytes, total, bytes.length - total);
			total+=i;
		}
		String contents = new String(bytes);
		Matcher matcher = _dojoBuildPattern.matcher(contents);
		if (matcher.find()) {
			String versionId = matcher.group(1);
			trace("  found Dojo version string: " + versionId);
			ModuleRecord mr = getApprovedModules().findModuleByHash(versionId);
			if (mr == null) {
				rejection.append("VERSION " + versionId + " NOT RECOGNIZED");
			} else {
				return mr;
			}
		} else {
			trace("  could not locate Dojo version string");
			rejection.append("COULD NOT DETERMINE DOJO VERSION");
		}
		return null;
	}
}
