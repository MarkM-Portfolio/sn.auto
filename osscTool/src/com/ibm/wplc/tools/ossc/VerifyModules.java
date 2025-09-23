/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* (C) Copyright IBM Corp. 2001, 2007                                */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.wplc.tools.ossc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Given a master list of files that have been approved for distribution with
 * the product, examine the modules in the identified repository and report on
 * what is recognized as approved.
 */
public class VerifyModules {

	private int jarCount = 0;
	
    public  static final String VERSION = "0.8";
    private ApprovedModules _approvedModules = new ApprovedModules();
    private IgnoreModules   _ignoreModules   = new IgnoreModules();
    private MessageDigest   _algorithm = null;
    private CommandLine     _line      = null;
    private Options         _options   = null;
    private Recorder        _recorder;
    
    synchronized int getJarCount(){
    	jarCount++;
    	return jarCount;
    }

    /**
     * @param args
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        VerifyModules verifier = new VerifyModules();
        verifier.analyze(args);

        int count = verifier.getJarCount()-1;
        String workArea = verifier.getWorkArea();
        verifier = null;
        Cleanup c = new Cleanup(count,workArea);
        c.run();

    }

    public String getWorkArea(){
    	return _approvedModules.getWorkArea();
    }
    /**
     * Analyze the files or archive specified comparing it to a library of known JARs and 
     * javascript files and produce a report detailing the what files are found (specifically 
     * jars and some javascript files).
     * 
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public void analyze(String[] args) throws FileNotFoundException, IOException, NoSuchAlgorithmException {

        CommandLineParser parser = new PosixParser();

        _options = getCLIOptions(args);

        try {
            _line = parser.parse(_options, args);
            
            // if ask for help, print and return
            if (_line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("VerifyModules [option] file1 [file2] [file3] [file...]\nfile arguments may be a directory, EAR, WAR or JAR and may include wildcards.", _options);
                return;
            }
            // if ask for version, print and return
            if (_line.hasOption("version")) {
                System.out.println("version " + VERSION);
                return;
            }
            // look for targets, if they are specified, we read in the approved list and process
            String targets[] = _line.getOptionValues("targets");
            if (targets.length == 0) {
                System.err.println("You must specify a JAR, WAR, EAR or directory to process");
            }
            else {
            	// look up approved modules
                if (_line.hasOption("approvedJars")) {
                	_approvedModules.load(_line.getOptionValue("approvedJars"));
                }
                else{
                	System.err.println("You must specify an approved list file"); 
                }
                // look up ignore files
                if (_line.hasOption("ignoreFile")) {
                	_ignoreModules.load(_line.getOptionValue("ignoreFile"));
                }
                // this is the main processing algorithm
                processTargets(targets);
            }
        }
        catch (ParseException pe) {
            System.err.println("Error parsing command line: " + pe);
            pe.printStackTrace(System.err);
        }
    }

    /**
     * Process each argument which could be a EAR, WAR, JAR, Directory or a
     * wildcard. Wildcards are expanded, directories traversed, processing all
     * WAR/EAR files and jars.
     * 
     * @param targets files to process
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private void processTargets(String[] targets) throws NoSuchAlgorithmException, IOException {
        _algorithm = MessageDigest.getInstance("MD5");
        _recorder = new Recorder();
        _recorder.setCommandLine(_line);
        for (int j = 0; j < targets.length; j++) {
        	// trace 
        	VerifyModulesUtil.trace(_line,"processing target " + targets[j]);
            // begin processing
        	File f = VerifyModulesUtil.getFile( targets[j]);
        	if (!f.exists()) {
        		System.err.println("'" + targets[j] + "' can not be found.");
        	}
        	else {
        		if (f.isDirectory()) {
        			processDirectory(f);
        		}
        		else if ( f.getName().endsWith(".jar") ||
        				  f.getName().endsWith(".ear") ||
        				  f.getName().endsWith(".war") ||
        				  f.getName().endsWith(".zip")  ) {
        			FileWrapper fw = new FileWrapper(f.getName(),f);
        			processArchive(fw,null);
        		}
        		else {
        			//processFile(f);
        			FileWrapper fw = new FileWrapper(f.getName(),f);
        			processFile(fw);
        		}
        		_recorder.report(f,_approvedModules);
        		_recorder.clear();
        	}
        }
    }

    private void processDirectory(File fdir) throws IOException {
        try {
            _recorder.addContainer(fdir.getName());
            VerifyModulesUtil.trace(_line,"process directory " + fdir.getAbsolutePath());
            if (!fdir.exists()) {
                throw new FileNotFoundException(fdir.getCanonicalPath());
            }

            FilenameFilter directoriesOnly = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return new File(dir.getAbsolutePath() + "/" + name).isDirectory();
                }
            };

            FilenameFilter filesOnly = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return new File(dir.getAbsolutePath() + "/" + name).isFile();
                }
            };

            File subDirs[] = fdir.listFiles(directoriesOnly);
            if (subDirs != null) {
                for (int i = 0; i < subDirs.length; i++) {
                    processDirectory(subDirs[i]);
                }
            }

            File files[] = fdir.listFiles(filesOnly);
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    FileWrapper fw = new FileWrapper(files[i].getName(),files[i]);
                    processFile(fw);
                }
            }
        } finally {
            _recorder.removeLastContainer();
        }
    }

    private void processArchive( FileWrapper fw, InputStream is) throws IOException{
    	try{
    		_recorder.addContainer(fw.getInternalName());
            // get the extension type of this archive (we are looking for war, ear, zip, jar)
            int lastDotPos = fw.getInternalName().lastIndexOf(".");
            String archiveExtType = fw.getInternalName().substring(lastDotPos+1);
            // see if we have embedded archives or if this is a leaf jar
            boolean isComposite = false;
            if (archiveExtType.equals("jar") ){
            	isComposite = hasEmbeddedArchive(fw.getFileOnDisk());
            }
            if ( archiveExtType.equals("ear")    ||
            		archiveExtType.equals("war") ||
            		archiveExtType.equals("zip") ||
            		archiveExtType.equals("jar") && isComposite == true){
                if (false && fw.getFileOnDisk().isDirectory()) {
                    processDirectory(fw.getFileOnDisk());
                } else {
                    FileInputStream fis = null;
                    ZipInputStream zis = null;
                    try{
                        if (is == null) {
                            File archiveFile = fw.getFileOnDisk();
                            if (!archiveFile.exists()) {
                                throw new FileNotFoundException(fw.getFileOnDisk().getCanonicalPath());
                            }
                            //_recorder.addContainer(fw.getInternalName());
                            fis = new FileInputStream(archiveFile);
                            zis = new ZipInputStream(fis);
                        }
                        else {
                            //_recorder.addContainer(fw.getInternalName());
                            zis = new ZipInputStream(is);
                        }
                        ZipEntry en;
                        while ((en = zis.getNextEntry()) != null) {
                            String fileName = en.getName();
                            int dotPos = fileName.lastIndexOf(".");
                            String extType = fileName.substring(dotPos+1);
                            // if we have an ear, war, or zip we process as an archive.
                            if (extType.equals("ear") || extType.equals("war") || extType.equals("zip")) {
                                // pass the current container set to the ignore modules to see if there is a hit
                                // probably could use better encapsulation with a Container object.
                                if ( doIgnore(en,fileName) == false ){
                                    FileWrapper newFW = new FileWrapper(en.getName(),null);
                                    processArchive(newFW, zis);
                                }
                            }
                            else if ( extType.equals("jar") ){
                                // extract the jar and process it
                                File extractFile = extractJar(en,fileName,zis);
                                FileWrapper extractFW = new FileWrapper(fileName,extractFile);
                                processFile(extractFW);
                            }
                            else{
                                // handle this entry
                                handleEntry(en,zis,extType,fileName);
                            }
                        }
                    }
                    finally{
                        if ( is == null ){
                            if (zis != null) zis.close();
                            if (fis != null) fis.close();
                        }
                    }
                }
            }
            else{
            	handleFile(fw,archiveExtType);
            }
    	}
        finally{
            _recorder.removeLastContainer();
        }
    }

    private void processFile(FileWrapper fw) throws IOException, FileNotFoundException {
    	VerifyModulesUtil.trace(_line,"process file " + fw.getInternalName());
        String filename = fw.getFileOnDisk().getCanonicalPath();
        if (filename != null && filename.length() > 0) {
        	// process or delegate to handler the appropriate handler
        	int lastDotPos = filename.lastIndexOf(".");
            String extType = filename.substring(lastDotPos+1);
            // if we have an ear, war, or zip we process as an archive.
            if ( extType.equals("zip")      ||
            		extType.endsWith("war") ||
            		extType.endsWith("ear") ||
            		extType.endsWith("jar") ) {
                if (fw.getFileOnDisk().isFile())
                    processArchive(fw, null);
                else
                    processDirectory(fw.getFileOnDisk());
            }
            else{
            	// process the passed in file (it is not a ear, war, zip, jar if we got this far
            	handleFile(extType,fw);
            }
        }
    }

    private File extractJar(ZipEntry en, String fileName, ZipInputStream zis) throws IOException, FileNotFoundException {
    	// assume this is a jar
    	String extractName = _approvedModules.getWorkArea()+getJarCount()+".jar";
		// trace what is happening
    	VerifyModulesUtil.trace(_line,"extract jar " + en.getName() + "to file " + extractName);
        // write the file
		VerifyModulesUtil.writeFile(extractName, zis);
		File rtnVal = new File(extractName);
		return rtnVal;
    }

    private boolean hasEmbeddedArchive(File archiveFile) throws FileNotFoundException, IOException{
    	boolean rtnVal = false;
        FileInputStream fis = null;
        ZipInputStream zis = null;
        try {
        	fis = new FileInputStream(archiveFile);
        	zis = new ZipInputStream(fis);
        	ZipEntry en;
        	while ((en = zis.getNextEntry()) != null) {
        		String fileName = en.getName();
        		File f = new File(fileName);
        		fileName = f.getName();
        		int lastDotPos = fileName.lastIndexOf(".");
        		String extType = fileName.substring(lastDotPos+1);
        		// if we have an ear, war, or zip we process as an archive.
        		if ( extType.equals("jar")    ||
        				extType.equals("war") ||
        				extType.equals("ear") ||
        				extType.equals("zip" ) ||
        				_approvedModules.findModuleByName(fileName) != null){
        			rtnVal = true;
        			break;
        		}
        	}
        	return rtnVal;
        }
        finally{
        	if ( zis != null ) zis.close();
        	if ( fis != null ) fis.close();
        }
    }

    private void handleFile(String extType, FileWrapper fw) throws IOException{
    	// extType and filename are passed in for convenience
        if ( _ignoreModules.isIgnoreFile(fw,_recorder.getContainerList()) == true ){
        	StringBuffer rejection = new StringBuffer();
        	_recorder.addIgnore(fw.getInternalName(), rejection, "");
        	VerifyModulesUtil.trace(_line,fw.getInternalName() + " is on ignore list");
        }
        else{
        	VerifyFileHandler handler = _approvedModules.getFileHandler(extType);
        	if ( handler != null ){
        		handler.setApprovedModules(_approvedModules);
        		handler.setAlgorithm(_algorithm);
        		handler.setRecorder(_recorder);
        		handler.setCommandLine(_line);
        		handler.handle(fw);
        	}
        	else{
        		VerifyModulesUtil.trace(_line,"skipping " + fw.getInternalName() + " no handler found");
        	}
        }
    }

    private void handleEntry(ZipEntry entry, ZipInputStream zis, String extType, String fileName) throws IOException{
    	// extType and filename are passed in for convenience
		if ( _ignoreModules.isIgnoreFile(entry,_recorder.getContainerList()) ==  true ){
			StringBuffer rejection = new StringBuffer();
			_recorder.addIgnore(fileName, rejection, "");
			VerifyModulesUtil.trace(_line,_recorder.getContainer() + entry.getName() + " on ignore list");
		}
		else{
			VerifyFileHandler handler = _approvedModules.getFileHandler(extType);
			if ( handler != null ){
				handler.setApprovedModules(_approvedModules);
				handler.setAlgorithm(_algorithm);
				handler.setRecorder(_recorder);
				handler.setCommandLine(_line);
				handler.handle(entry,zis);
			}
			else {
				VerifyModulesUtil.trace(_line,"skipping " + _recorder.getContainer() + entry.getName() + " no handler found");
			}
		}
    }

    private void handleFile(FileWrapper fw, String extType) throws IOException{
		if ( _ignoreModules.isIgnoreFile(fw,_recorder.getContainerList()) ==  true ){
			StringBuffer rejection = new StringBuffer();
			_recorder.addIgnore(fw.getInternalName(), rejection, "");
			VerifyModulesUtil.trace(_line,_recorder.getContainer() + fw.getInternalName() + " on ignore list");
		}
		else{
			VerifyFileHandler handler = _approvedModules.getFileHandler(extType);
			if ( handler != null ){
				handler.setApprovedModules(_approvedModules);
				handler.setAlgorithm(_algorithm);
				handler.setRecorder(_recorder);
				handler.setCommandLine(_line);
				handler.handle(fw);
			}
			else {
				VerifyModulesUtil.trace(_line,"skipping " + _recorder.getContainer() + fw.getInternalName() + " no handler found");
			}
		}
    }

    private boolean doIgnore(ZipEntry entry, String fileName) throws IOException{
    	boolean rtnVal = _ignoreModules.isIgnoreFile(entry,_recorder.getContainerList());
    	if ( rtnVal == true ){
    		StringBuffer rejection = new StringBuffer();
    		_recorder.addIgnore(fileName, rejection, "");
    		VerifyModulesUtil.trace(_line,_recorder.getContainer() + entry.getName() + " on ignore list");
    	}
    	return rtnVal;
    }

    @SuppressWarnings("static-access")
    private Options getCLIOptions(String args[]) {
        Options options = new Options();
        Option approvedFile = OptionBuilder.hasArg()
                                        .withDescription("The file defining the approved artifacts.")
                                        .create("approvedJars");
        options.addOption(approvedFile);

        Option includeHash = OptionBuilder.withDescription("Display the hash value for any mismatches").create("hash");
        options.addOption(includeHash);

        Option help = new Option("help", "Print this message.");
        options.addOption(help);

        Option version = new Option("version", "Show the version number of this tool.");
        options.addOption(version);

        Option fullPath = new Option("fullPath", "Report the full file path in reports");
        options.addOption(fullPath);
        
        Option verbose = new Option("verbose", "Run in verbose mode.");
        options.addOption(verbose);

        Option recurseJars = new Option("recurseJars", "Processing should recurse jars to leaf nodes");
        options.addOption(recurseJars);
     
        Option hits = new Option("hits", "Display a report of artifacts that match the approved module list specifications.");
        options.addOption(hits);

        Option ignores = new Option("ignores", "Display a report of artifacts that are ignores as per the ignored files specification.");
        options.addOption(ignores);

        Option productSource = new Option("self", "Display a report of artifacts that match specifications for product code.");
        options.addOption(productSource);
        
        Option ibmSource = new Option("ibm", "Display a report of artifacts that match specifications for IBM code.");
        options.addOption(ibmSource);

        Option allMisses = new Option("allMisses", "Display a report of artifacts that match specifications for IBM code.");
        options.addOption(allMisses);
        
        Option sortByModule = new Option("sortByModule", "Sort the hits organized by approved module name.");
        options.addOption(sortByModule);
        
        Option targets = OptionBuilder.hasArgs()
                                      .withDescription("File defining artifacts to process.")
                                      .create("targets");
        options.addOption(targets);
        
        Option ignore = OptionBuilder.hasArg()
                                     .withDescription("File defining artifacts to ignore.")
                                     .create("ignoreFile");
        options.addOption(ignore);

        return options;
    }
}

class Cleanup{
	 private int count;
	 private String workArea;
	 Cleanup( int count, String workArea ){
		 this.count = count;
		 this.workArea = workArea;
	 }
	 void run(){
		 for( int i = 1 ; i < count+1 ; i++ ){
			 File f = new File(workArea+i+".jar");
			 // Make sure the file or directory exists and isn't write protected
			 if (f.exists() == true ){
				 boolean success = f.delete();
				 if (!success){
					 System.out.println("Failed to cleanup file "+workArea+i+".jar" );
				 }
			 }
		 }
	 }
}