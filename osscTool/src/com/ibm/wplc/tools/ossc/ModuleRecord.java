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


/**
 * ModuleRecord represents key information about a module (jar or javascript), including the filename, 
 * MD5 hash signature (always a 32 character hexadecimal number), and optional
 * a textual based version identifier.
 */
public class ModuleRecord implements Cloneable {

    private String name;
    private String version;
    private String md5Hash;
    private String comment;
    private String directoryPath;
    
    public ModuleRecord(String name, String version, String md5, String comment) {
        this.name = name;
        this.version = version;
        this.md5Hash = md5;
        this.comment = comment;
    }
    
    public ModuleRecord() {
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getMd5Hash() {
        return md5Hash;
    }
    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }
    
    public ModuleRecord clone() {
        ModuleRecord mr = new ModuleRecord();
        if (comment != null)
            mr.setComment(new String(this.comment));
        if (directoryPath != null)
            mr.setDirectoryPath(new String(this.directoryPath));
        if (md5Hash != null) 
            mr.setMd5Hash(new String(this.md5Hash));
        if (name != null)
            mr.setName(new String(this.name));
        if (version != null)
            mr.setVersion(new String(this.version));
        
        return mr;
    }
    
    public ModuleRecord clone(String name) {
        ModuleRecord mr = clone();
        mr.setName(name);
        return mr;
    }
    
    
}
