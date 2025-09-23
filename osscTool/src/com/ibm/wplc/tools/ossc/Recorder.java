package com.ibm.wplc.tools.ossc;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;

public class Recorder {

    private ArrayList<String>       _container = new ArrayList<String>();
    private ArrayList<ModuleRecord> _hits = new ArrayList<ModuleRecord>();
    private ArrayList<ModuleRecord> _misses = new ArrayList<ModuleRecord>();
    private ArrayList<ModuleRecord> _ignores = new ArrayList<ModuleRecord>();
    private ArrayList<ModuleRecord> _ibmDeveloped = new ArrayList<ModuleRecord>();
    private ArrayList<ModuleRecord> _selfDeveloped = new ArrayList<ModuleRecord>();
    private TreeSet<String> _packagesUsed = new TreeSet<String>();
    private CommandLine     _line = null;

    void setCommandLine(CommandLine line){
    	_line = line;
    }

    void addContainer(String containerName){
        if (_container.size() == 0 || (_container.size() > 0 && !containerName.contentEquals(_container.get(_container.size() -1)))) {
        	_container.add(containerName);
        	System.err.println("processing " + getContainer());
        }
    }

    String getContainer(){
        if (_line.hasOption("fullPath")) {
            StringBuffer sb = new StringBuffer();
            for (int i=0; i< _container.size(); i++) {
                sb.append(_container.get(i));
                if ( i < _container.size()-1 ){
                	sb.append(":");
                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }

   List<String> getContainerList(){
    	return _container;
    }

    void removeLastContainer(){
        if (_container.size() > 0) {
            _container.remove(_container.size() - 1);
        }
    }

    /**
     * Record the entry to the list of matches
     * 
     * @param localFileName
     * @param approvedModule
     */
    protected void addHit(String localFileName, ModuleRecord approvedModule) {
        ModuleRecord mr = (ModuleRecord) approvedModule.clone(localFileName);
        mr.setDirectoryPath(getContainer());
        _hits.add(mr);
        VerifyModulesUtil.trace(_line,"  +++hit+++");
        _packagesUsed.add(approvedModule.getVersion());
    }

    /**
     * Record the entry to the list of non-matched files
     * 
     * @param localJarName
     * @param hash
     */
    protected void addMiss(String localJarName, StringBuffer rejection, String hash) {
    	String comment = (rejection == null ? null : rejection.toString());
    	ModuleRecord mr = new ModuleRecord(localJarName, null, hash, comment);
    	mr.setDirectoryPath(getContainer());
    	_misses.add(mr);
    	VerifyModulesUtil.trace(_line,"  ---miss---");
    }

    protected void addIgnore(String localJarName, StringBuffer rejection, String hash) {
    	String comment = (rejection == null ? null : rejection.toString());
    	ModuleRecord mr = new ModuleRecord(localJarName, null, hash, comment);
    	mr.setDirectoryPath(getContainer());
    	_ignores.add(mr);
    	VerifyModulesUtil.trace(_line,"  ---miss---");
    }

    /**
     * Record this as a probable-IBM jar - - the jar doesn't match any Hashed modules, but the 
     * classes in the JAR file all come from expected packages.
     * 
     * @param localJarName
     * @param hash
     */
    protected void addSelfDeveloped(String localJarName, StringBuffer rejection, String hash) {
        String comment = (rejection == null ? null : rejection.toString());
        ModuleRecord mr = new ModuleRecord(localJarName, null, hash, comment);
        mr.setDirectoryPath(getContainer());
    	VerifyModulesUtil.trace(_line,"  ...selfDeveloped");
        _selfDeveloped.add(mr);
    }

    /**
     * Record this as a probable-IBM jar - - the jar doesn't match any Hashed modules, but the 
     * classes in the JAR file all come from expected packages.
     * 
     * @param localJarName
     * @param hash
     */
    protected void addIBMDeveloped(String localJarName, StringBuffer rejection, String hash) {
        ModuleRecord mr = new ModuleRecord(localJarName, null, hash, rejection.toString());
        mr.setDirectoryPath(getContainer());
    	VerifyModulesUtil.trace(_line,"  ...ibmDeveloped");
        _ibmDeveloped.add(mr);
    }

    /**
     * Print a report detailing jars that matched and jars that didn't match
     * 
     * @param source file (WAR/EAR/JAR or Directory) processed
     * @throws IOException 
     */
    @SuppressWarnings("unchecked")
    void report(File sourceFile, ApprovedModules approvedModules) throws IOException {
    	String source = sourceFile.getCanonicalPath();
    	SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");
    	SimpleDateFormat fdf = new SimpleDateFormat("MM/dd/yy HH:mm a");

    	System.out.println("\n");
    	if (sourceFile.isFile()) {
    		System.out.println("Analysis of " + source +" (dated " + fdf.format(new Date(sourceFile.lastModified())) + ", size " + NumberFormat.getInstance(Locale.US).format(sourceFile.length()) +" bytes)");
    	} else {
    		System.out.println("Analysis of " + source);
    	}
    	System.out.println("Compared against " + approvedModules.get_approvalMessage());
    	System.out.println("with Open Source Module Verifier version " + VerifyModules.VERSION + " on " + formatter.format(new Date(System.currentTimeMillis())));

    	Iterator<String> i = _packagesUsed.iterator();
    	if (i.hasNext()) {
        	System.out.println("\n");
    		System.out.println("===========================");
    		System.out.println("3rd Party Modules Included:");
    		System.out.println("---------------------------");
    		while (i.hasNext()) {
    			System.out.println(i.next());
    		}
    	}

    	if (_line.hasOption("hits") &&
    		 _hits.size() > 0       ) {
    		System.out.println("\n");
            System.out.println("==========================");
    		System.out.println("MD5 Hash Signature Matches:");
    		System.out.println("---------------------------");
    		reportHits();
    	}
    	if ( ( _line.hasOption("self") || _line.hasOption("allMisses") ) &&
    		  _selfDeveloped.size() > 0 )  {
    		System.out.println("\n");
    		System.out.println("===========================");
    		System.out.println("Non-Matched Jars, appear to be product related:");
    		System.out.println("---------------------------");
    		reportMisses(_selfDeveloped);
    	}
    	if ( (_line.hasOption("ibm") ||_line.hasOption("allMisses") ) &&
    		 _ibmDeveloped.size() > 0) {
    		System.out.println("\n");
    		System.out.println("===========================");
    		System.out.println("Non-Matched Jars, appear to contain non product but approved IBM developed clases:");
    		System.out.println("---------------------------");
    		reportMisses(_ibmDeveloped);
    	}
    	if ( _line.hasOption("ignores") &&
    		 _ignores.size() > 0) {
    		System.out.println("\n");
    		System.out.println("===========================");
    		System.out.println("Ignored modules");
    		System.out.println("---------------------------");
    		reportMisses(_ignores);
    	}
    	if ( _misses.size() > 0){
    		System.out.println("\n");
    		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    		System.out.println("Non-Matched Files, manual examination required:");
    		System.out.println();
    		reportMisses(_misses);
    		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");         
    	}
    	else{
    		System.out.println("\n");
    		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    		System.out.println("No non-matched files found.");
    		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");  
    	}
    }

    private void reportHits() {
        boolean fullPath = _line.hasOption("fullPath");
        boolean sortByModule = _line.hasOption("sortByModule");
        
        if (sortByModule) {
            Collections.sort(_hits, new PackageComparator());
            Iterator<ModuleRecord> hitIterator = _hits.iterator();
//            String previousPackage = null;
            while (hitIterator.hasNext()) {
                ModuleRecord mr = hitIterator.next();
//                if (previousPackage != null) { 
//                    if (!previousPackage.equals(mr.getVersion())) {
//                        System.out.println();
//                    }
//                }
//                previousPackage = mr.getVersion();
                StringBuffer sb = new StringBuffer(120);
                sb.append(mr.getVersion());
                sb.append("\t");
                if (fullPath) {
                    sb.append(mr.getDirectoryPath());
                }
                String moduleName = mr.getName();
                if ( mr.getDirectoryPath().endsWith(moduleName)== false ){
                    sb.append(":");
                    sb.append(moduleName);
                }
                System.out.println(sb.toString());
            }
        } else {
            Iterator<ModuleRecord> hitIterator = _hits.iterator();
            while (hitIterator.hasNext()) {
                ModuleRecord mr = hitIterator.next();
                StringBuffer sb = new StringBuffer(120);
                if (fullPath) {
                    sb.append(mr.getDirectoryPath());
                }
                String moduleName = mr.getName();
                if ( mr.getDirectoryPath().endsWith(moduleName)== false ){
                    sb.append(":");
                	sb.append(moduleName);
                }
                sb.append("  *  ");
                sb.append(mr.getVersion());
                System.out.println(sb.toString());
            }
        }
    }

    private void reportMisses(ArrayList<ModuleRecord> modules) {
        int maxPathLen = 0;
        int currentLen = 0;
        for (ModuleRecord moduleRecord : modules) {
            currentLen = moduleRecord.getName().length();
            if (_line.hasOption("fullPath")) 
                currentLen += moduleRecord.getDirectoryPath().length() + 1;
            if (currentLen > maxPathLen) {
                maxPathLen = currentLen;;
            }
        }
        
        //String format = "%1$-" + maxPathLen + 3 + "s%2s\n";
        //System.out.format(format, "First", "Last");
        StringBuffer sb = new StringBuffer();
        Iterator<ModuleRecord> moduleIterator = modules.iterator();
        while (moduleIterator.hasNext()) {
        	sb.setLength(0);
            ModuleRecord mr = moduleIterator.next();
            if (_line.hasOption("fullPath")) {
            	sb.append(mr.getDirectoryPath() + ":");
            }
            String moduleName = mr.getName();
            if ( mr.getDirectoryPath().endsWith(moduleName)== false ){
            	sb.append(moduleName);
            }
            if (mr.getComment() != null) {
            	sb.append("\t");
            	sb.append(mr.getComment());
            }
            if (_line.hasOption("hash")) {
            	sb.append("\t");
            	sb.append(mr.getMd5Hash());
            }
            System.out.println(sb.toString());
        }
    }

    void clear(){
        _hits.clear();
        _misses.clear();
        _ignores.clear();
        _ibmDeveloped.clear();
        _selfDeveloped.clear();
        _packagesUsed.clear();
        _container.clear();
    }

    class PackageComparator implements Comparator<ModuleRecord> {

        public int compare(ModuleRecord o1, ModuleRecord o2) {
            String one = o1.getVersion();
            String two = o2.getVersion();
            if (two == null) {
                return -1;
            } else if (one == null) {
                return 1;
            }
            return one.compareTo(two);
        }
    }
}
