package com.ibm.wplc.tools.ossc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

public class IgnoreModules {

    //private final String DEFAULT_FNAME = "ignore-modules.list";
    private final HashMap<String, IgnoreRecord> recordsByName = new HashMap<String, IgnoreRecord>();
    private final String FILE_DELIM = "!file:";
    private final String COMMENT_DELIM = "#";
    private final String IGNORE_FILE_DELIM = "!ignore_file_regex:";
    private String _ignoreRE = null;
    private Pattern _ignorePattern = null;
    
    /**
     * Load the approved modules list from the classpath
     * @throws FileNotFoundException
     * @throws IOException
     */
//    public void load() throws FileNotFoundException, IOException {
//        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream (DEFAULT_FNAME);
//        readEntries(resourceAsStream);
//        resourceAsStream.close();
//        init();
//    }

    /**
     * Load the approved modules list from the specified file
     * @param fname path & filename of the approved modules list
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void load(String fname) throws FileNotFoundException, IOException {
        File f = new File(fname);
        if (!f.exists()) {
            throw new FileNotFoundException(f.getCanonicalPath());
        }
        FileInputStream fis = new FileInputStream(fname);
        readEntries(fis);
        fis.close();
        init();
    }

    private void init() {
    	if( _ignoreRE != null ){
    	    _ignorePattern = Pattern.compile(_ignoreRE);
    	}
    }

    /**
     * Process the contents of the approved modules file
     * 
     * @param f
     * @throws FileNotFoundException
     */
    public final void readEntries(InputStream is) throws FileNotFoundException {
        Scanner scanner = new Scanner(is);
        while (scanner.hasNextLine()) {
            processLine(scanner.nextLine());
        }
        scanner.close();
    }
    
    private int lineNumber = 0;
    /**
     * Process a line (module entry) from the approved modules file
     * 
     * @param aLine Format is name<tab>md5<tab>version description
     */
    private void processLine(String aLine) {
        lineNumber++;
        // Skip over blank lines or lines that begin with the comment character
        if (aLine == null || aLine.length() == 0) {
            return;
        }
        else if (aLine.startsWith(COMMENT_DELIM)) {
            return;
        }
        else if (aLine.startsWith("!")) {
        	if (aLine.startsWith(FILE_DELIM)){
        		processFile(aLine);
        	}
        	else if (aLine.startsWith(IGNORE_FILE_DELIM)){
                _ignoreRE = aLine.substring(IGNORE_FILE_DELIM.length());
                _ignoreRE = _ignoreRE.trim();
                if ( _ignoreRE.length() == 0 ){
                	_ignoreRE = null;
                }
        	}
        }
        else {
            System.out.println("Unrecoginized config spec entry at line " + lineNumber); 
            System.out.println("Contents: " + aLine);
        }
    }

    private void processFile(String aLine){
    	// we expect a line of the form "handlerprefix\textension\tclassname
    	// e.g. !file somename 
        Scanner scanner = new Scanner(aLine); // default delimeter is whitespace
        String[] args = new String[3];
        int i = 0;
        for (i = 0 ; i < 3 ; i++ ){
        	if ( scanner.hasNext() ){
        		args[i]= scanner.next();
        		args[i] = args[i].trim();
        		//args[i] = args[i].toLowerCase();
        	}
        	else{
        		break;
        	}
        }
        // if we found three arguments, we assume they suffice
        // ow, we know we have an error
        if (i  >= 2 ) {
        	// arg[2] could be null, that just means no container.
        	IgnoreRecord ignoreR = recordsByName.get(args[1]);
        	if ( ignoreR == null ){
        		ignoreR = new IgnoreRecord(args[1]);
        		recordsByName.put(args[1],ignoreR);
        	}
        	ignoreR.addContainerName(args[2]);
        }
        else{
            System.err.println("Invalid line. Unable to process: " + aLine);
        }
        scanner.close();
    }

    /**
     */
    boolean isIgnoreFile(FileWrapper fw, List<String> containerList) throws IOException {
    //boolean isIgnoreFile(File f, List<String> containerList) throws IOException {
    //  String name = f.getCanonicalPath().toLowerCase();
    	String name = fw.getInternalName();
    	return isOnIgnoreList(name, containerList);
    }

    boolean isIgnoreFile(ZipEntry entry, List<String> containerList) throws IOException {
    	String name = entry.getName();
    	return isOnIgnoreList(name, containerList);
    }
    /*
     * Determine if a target files is on an ignore list. We look to see if there is an ignore
     * record for this target record. If so, we then see if any of the associated containers
     * are on the current set of containers.
     */
    private boolean isOnIgnoreList( String filename, List<String> containerList ){
    	boolean rtnVal = false;
    	
    	// first look at the ignore pattern
    	if (_ignorePattern != null) {
    		Matcher matcher = _ignorePattern.matcher(filename);
    		if (matcher.find()) {
    			return true;
    		}
    	}

    	//System.out.println("**** isOnIgnoreList: " + filename);
    	// ow, look through file specifications.
    	IgnoreRecord ir = recordsByName.get(filename);
    	if ( ir != null ){
        	// if we found the file on the ignore list, we must cycle through the
    		// passed in container set and see if we have any matches for this file
    		Iterator<String> ignoreIter = ir.getContainerNames();
    		String ignoreContainer;
    		while ( ignoreIter.hasNext()){
    			// this is a container specified in the ignore list
    			ignoreContainer = ignoreIter.next();

    			// a null container list (not specified in ignore list) means
    			// there is no container constraint and we can kick out.
    			if ( ignoreContainer == null){
    				rtnVal = true;
    				break;
    			} else{
    				// we cycle through the container names and see if we have a match
    				String containName;
    				Iterator<String> setIter = ir.getContainerNames();
    				while( setIter.hasNext()){
    					containName = setIter.next();
    					if ( containName.equals(ignoreContainer)){
    						rtnVal = true;
    						break;
    					}
    				}
    		    }
    			if ( rtnVal == true){
    				break;
    			}
    		}
    	}
        return rtnVal;
    }
}
