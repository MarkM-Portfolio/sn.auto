package com.ibm.wplc.tools.ossc;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.cli.CommandLine;

public abstract class VerifyFileHandler {

	private CommandLine     _line;
	private ApprovedModules _approvedModules;
	private MessageDigest   _algorithm;
	private Recorder        _recorder;

	abstract public void handle( FileWrapper fileWrapper ) throws IOException;

	abstract public void handle( ZipEntry entry, ZipInputStream zis ) throws IOException;

	void setApprovedModules( ApprovedModules approvedModules){
		_approvedModules = approvedModules;
	}

	public ApprovedModules getApprovedModules(){
		return _approvedModules;
	}

	void setAlgorithm( MessageDigest algorithm){
		_algorithm = algorithm;
	}

	protected MessageDigest getAlgorithm(){
		return _algorithm;
	}

	void setCommandLine(CommandLine line){
		_line = line;
	}

	protected CommandLine getCommandLine(){
		return _line;
	}

	void setRecorder( Recorder recorder ){
		_recorder = recorder;
	}

	protected void trace(String msg){
		VerifyModulesUtil.trace(_line,msg);
	}

	protected String getContainer(){
		return _recorder.getContainer();
	}

	protected void addSelfDeveloped(String localJarName, StringBuffer rejection, String hash){
        _recorder.addSelfDeveloped(localJarName, rejection, hash);
	}

	protected void addIBMDeveloped(String localJarName, StringBuffer rejection, String hash){
		_recorder.addIBMDeveloped(localJarName, rejection, hash);
	}

	protected void addMiss(String localJarName, StringBuffer rejection, String hash){
		_recorder.addMiss(localJarName, rejection, hash);
	}

	protected void addHit(String localFileName, ModuleRecord approvedModule){
		_recorder.addHit(localFileName, approvedModule);
	}
	
	protected void addApprovedWildcard(String wildApproval, String internalName, String hash) {
		_recorder.addIBMDeveloped(internalName, new StringBuffer(wildApproval), hash);
	}

}
