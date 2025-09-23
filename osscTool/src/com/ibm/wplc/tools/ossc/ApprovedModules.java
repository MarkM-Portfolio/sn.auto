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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Container for the OSSC Approved Module records
 */
public class ApprovedModules {

    //private final String DEFAULT_FNAME = "approved-modules.list";

    private final HashMap<String, ModuleRecord> modulesByHash    = new HashMap<String, ModuleRecord>();
    private final HashMap<String, ModuleRecord> modulesByName    = new HashMap<String, ModuleRecord>();
    private final HashMap<String, VerifyFileHandler>  handlersByExt    = new HashMap<String, VerifyFileHandler>();

    private final HashSet<String> jsFiles = new HashSet<String>();
    private final HashSet<String> gifFiles = new HashSet<String>();
    private String _approvalVerbage = null;
    private final String HANDLER_PREFIX = "!handler";
    private final String DATE_STANZA = "!module_listing_version:";
    private final String COMMENT_DELIM = "#";
    private final String WORK_AREA_DELIM = "!work_area:";
    private        String _workArea = null;
    private final String CONFIG_SPEC_VERSION_DELIM = "!config_spec_version:";
    private int _configSpecVersion = 0;
    private final String HOME_GROWN_REGEXP_DELIM = "!source_packages_regex:";
    private String _homeGrownRE = null;
    private Pattern _productPkgPattern = null;
    private final String IBM_DEVELOPED_REGEXP_DELIM = "!ibm_packages_regex:";
    private String _ibmDevelopedRE = null;
    private Pattern _ibmPkgPattern = null;

    String getWorkArea(){
    	return _workArea;
    }

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
    	if ( _homeGrownRE != null ) _productPkgPattern = Pattern.compile(_homeGrownRE);
    	if ( _ibmDevelopedRE != null ) _ibmPkgPattern = Pattern.compile(_ibmDevelopedRE);
    }
    
    /**
     * Determine if the contents of a JAR are from packages we recognize as being developed locally
     * 
     * @param is input stream for the archive (usually a JAR) contents
     * @param sb output buffer that is used to store non-matching package to
     * @return
     * @throws IOException
     */
    public boolean isSelfDeveloped(InputStream is, StringBuffer sb) throws IOException {
        boolean rtnVal = false;
        if (_productPkgPattern != null) {
            rtnVal = true;
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry en;
            HashSet<String> v = new HashSet<String>();
            while ( rtnVal && (en = zis.getNextEntry()) != null) {
                if (!en.isDirectory() && en.getName().endsWith(".class")){
        			//System.out.println("checking internal " +en.getName());
                    Matcher matcher = _productPkgPattern.matcher(en.getName());
                    if (!matcher.find()) {
                        rtnVal = false;}
                    else{
                        v.add(matcher.group());
                    }
                }
            }
            if (rtnVal) sb.append(v.toString());
        }
        return rtnVal;
//        ZipInputStream zis = new ZipInputStream(is);
//        ZipEntry en;
//        boolean ibm = true;
//        HashSet<String> v = new HashSet<String>();
//        
//        if (_lconnPackages != null) {
//            while (ibm && (en = zis.getNextEntry()) != null) {
//                if (!en.isDirectory() && en.getName().endsWith(".class")){
//                    Matcher matcher = _lconnPackages.matcher(en.getName());
//                    if (!matcher.find()) {
//                        ibm = false;
//                    } else {
//                        v.add(matcher.group());
//                    }
//                }
//            }
//        }
//        if (ibm) sb.append(v.toString());
//        return ibm;
    }
    
    
    /**
     * Determine if the contents of a JAR are from packages we recognize as being developed external
     * to the component being scanned but internal to IBM
     * 
     * @param is input stream for the archive (usually a JAR) contents
     * @param sb output buffer that is used to store non-matching package to
     * @return
     * @throws IOException
     */
    public boolean isIBMDeveloped(InputStream is, StringBuffer sb) throws IOException {
    	boolean rtnVal = false;
    	if (_ibmPkgPattern != null) {
    		rtnVal = true;
    		ZipInputStream zis = new ZipInputStream(is);
    		ZipEntry en;
    		HashSet<String> v = new HashSet<String>();
    		while (rtnVal && (en = zis.getNextEntry()) != null) {
    			if (!en.isDirectory() && en.getName().endsWith(".class")){
    				//ignore our own packages
    				if (!_productPkgPattern.matcher(en.getName()).find()) {
    					//System.out.println("checking " +en.getName());
    					Matcher matcher = _ibmPkgPattern.matcher(en.getName());
    					if (!matcher.find()) {
    						sb.append("\t* appears external, class sample: " + en.getName());
    						rtnVal = false;
    					} else {
    						v.add(matcher.group());
    					}
    				}
    			}
    		}
    		if (rtnVal) sb.append(v.toString());
    	}
    	return rtnVal;
    	
//        ZipInputStream zis = new ZipInputStream(is);
//        ZipEntry en;
//        boolean ibm = true;
//        HashSet<String> v = new HashSet<String>();
//        
//        if (_ibmPackages != null) {
//            while (ibm && (en = zis.getNextEntry()) != null) {
//                if (!en.isDirectory() && en.getName().endsWith(".class")){
//                    Matcher matcher = _ibmPackages.matcher(en.getName());
//                    if (!matcher.find()) {
//                        sb.append("\t* appears external, class sample: " + en.getName());
//                        ibm = false;
//                    } else {
//                        v.add(matcher.group());
//                    }
//                }
//            }
//        }
//        if (ibm) sb.append(v.toString());
//        return ibm;
    }
    

    /**
     * Return the date of the approval list
     * @return
     */
    public String get_approvalMessage() {
        return _approvalVerbage;
    }

    /**
     * Locate the Approved Module record given an MD5 hash
     * @param hash
     * @return
     */
    public ModuleRecord findModuleByHash(String hash) {
        return modulesByHash.get(hash.toUpperCase());

    }

    /**
     * Locate the Approved Module record given a module file name
     * @param name
     * @return
     */
    public ModuleRecord findModuleByName(String name) {
        return modulesByName.get(name);

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
        else if (aLine.startsWith("!")) {
            if (aLine.startsWith(DATE_STANZA)) {
                _approvalVerbage = aLine.substring(DATE_STANZA.length());
                _approvalVerbage = _approvalVerbage.trim();
                return;
            }
            if (aLine.startsWith(CONFIG_SPEC_VERSION_DELIM)) {
                _configSpecVersion = getCSVersion(aLine);
                return;
            }
            if (aLine.startsWith(WORK_AREA_DELIM)) {
                _workArea = aLine.substring(WORK_AREA_DELIM.length());
                _workArea = _workArea.trim();
                if ( _workArea == "" ) _workArea = ".";
                _workArea = _workArea+File.separator;
                System.out.println("\nUsing work area: " + _workArea);
                return;
            }
            if (aLine.startsWith(HOME_GROWN_REGEXP_DELIM)) {
                _homeGrownRE = aLine.substring(HOME_GROWN_REGEXP_DELIM.length());
                _homeGrownRE = _homeGrownRE.trim();
                if ( _homeGrownRE.equals("")){
                	_homeGrownRE = null;
                }
                return;
            }
            if (aLine.startsWith(IBM_DEVELOPED_REGEXP_DELIM)) {
                _ibmDevelopedRE = aLine.substring(IBM_DEVELOPED_REGEXP_DELIM.length());
                _ibmDevelopedRE = _ibmDevelopedRE.trim();
                if ( _ibmDevelopedRE.equals("")){
                	_ibmDevelopedRE = null;
                }
                return;
            }
            if ( aLine.startsWith(HANDLER_PREFIX)){
            	processHandler(aLine);
            	return;
            }
            else {
                System.out.println("Unrecoginized config spec entry at line " + lineNumber); 
                System.out.println("Contents: " + aLine);
                throw new RuntimeException("Invalid Configuration file");
            }
        } else if (aLine.startsWith(COMMENT_DELIM)) {
            return;
        }
        
        // non blank/comment line, process module record
        ModuleRecord dup = null;
        Scanner scanner = new Scanner(aLine);
        //scanner.useDelimiter("\t");
        if (scanner.hasNext()) {
            ModuleRecord module = new ModuleRecord();
            module.setName(scanner.next());
            module.setMd5Hash(scanner.next());
            if (scanner.hasNext()) {
                module.setVersion(scanner.next());
            }
            if (scanner.hasNext()) {
            	// here we just read the rest of the line
            	String comment = scanner.nextLine();
            	comment = comment.trim();
                module.setComment(comment);
            }
            dup = modulesByName.put(module.getName(), module);
            if (dup != null) {
                if (isEqual(dup.getVersion(), module.getVersion())) {
                    //System.err.println("Warning, module name exists twice in the approved list: " + module.getName());
                    //System.err.println("offending entry: " + aLine);
                }
            }
            dup = modulesByHash.put(module.getMd5Hash(), module);
            if (false && (dup != null)) {
                System.out.println("\n\nError, MD5 Hash is not unique at line " + lineNumber + " for \n" + module.getName() + ", hash=" + module.getMd5Hash());
                System.out.println("\n\nPrevious entry: \n" + dup.getName() + "  " + dup.getMd5Hash() + "\n\n");
                throw new RuntimeException("Duplicate Module MD5 signature");
            }
            
            if (module.getName().endsWith(".js")) {
                jsFiles.add(module.getName());
            } else if (module.getName().endsWith(".gif")) {
                gifFiles.add(module.getName());
            }
        } else {
            System.out.println("Invalid line. Unable to process: " + aLine);
        }
        scanner.close();
    }

    private void processHandler(String aLine){
    	// we expect a line of the form "handlerprefix\textension\tclassname
    	// e.g. !handler_js	.js	com.xxx.yyy.myJsHandler 
        Scanner scanner = new Scanner(aLine);
        //scanner.useDelimiter("\t"); // could use any white space?
        String[] args = new String[3];
        int i = 0;
        for (i = 0 ; i < 3 ; i++ ){
        	if ( scanner.hasNext() ){
        		args[i]= scanner.next();
        		args[i] = args[i].trim();
        	}
        	else{
        		break;
        	}
        }
        // if we found three arguments, we assume they suffice
        // ow, we know we have an error
        if (i == 3) {
        	try{
        	    VerifyFileHandler handler = (VerifyFileHandler)(Class.forName(args[2]).newInstance());
        	    handlersByExt.put(args[1],handler);
        	}
        	catch( Exception ex ){
        		System.err.println("Error processing line: " + aLine);
        	}
        }
        else{
            System.err.println("Invalid line. Unable to process: " + aLine);
        }
    }

    public VerifyFileHandler getFileHandler( String extension ){
    	return handlersByExt.get(extension);
    }

    private int getCSVersion(String line) {
        int ver = -1;
        String strVer = line.substring(CONFIG_SPEC_VERSION_DELIM.length());
        strVer = strVer.trim();
        try {
            ver = Integer.parseInt(strVer);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("error parsing version number from " + line);
        }
        return ver;
    }
    
    public boolean jsNameMatch(String fname) {
        File f = new File(fname);
        if (jsFiles.contains(f.getName())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean gifNameMatch(String fname) {
        File f = new File(fname);
        if (gifFiles.contains(f.getName())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isEqual(String s1, String s2) {
        if (s1 != null && s2 != null) {
            return (s1.compareToIgnoreCase(s2) == 0 ? true : false);
        } else {
            return (false);
        }
    }
}
