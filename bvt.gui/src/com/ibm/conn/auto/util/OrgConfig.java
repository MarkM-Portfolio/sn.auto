package com.ibm.conn.auto.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OrgConfig {

	private static final Logger log = LoggerFactory.getLogger(OrgConfig.class);
	
	private String name;
	private String grpID;
	private String uri;
	private String orgID;
	private String subID;

	
	
	public OrgConfig(String name, String grpID, String uri, String orgID, String subID){
		this.setName(name);
		this.setGrpID(grpID);
		this.setURI(uri);
		this.setOrgID(orgID);
		this.setSubID(subID);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGrpID() {
		return grpID;
	}

	public void setGrpID(String grpID) {
		this.grpID = grpID;
	}
	
	public String getURI() {
		return uri;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}
	
	public String getOrgID() {
		return orgID;
	}

	public void setOrgID(String orgID) {
		this.orgID = orgID;
	}

	public String getSubID() {
		return subID;
	}

	public void setSubID(String subID) {
		this.subID = subID;
	}


	
	public static List<OrgConfig> loadOrgs(){

		List<OrgConfig> listOrgs = new ArrayList<OrgConfig>();
		
		try {
	    	 
	    	File fXmlFile = new File(TestConfigCustom.getInstance().getOrgXML_Path());
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(fXmlFile);

	    	doc.getDocumentElement().normalize();
	    	NodeList nList = doc.getElementsByTagName("org");

	    	for (int temp = 0; temp < nList.getLength(); temp++) {
	     
	    		Node nNode = nList.item(temp);
	  
	    		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	     
	    			Element eElement = (Element) nNode;

	    			OrgConfig org = new OrgConfig(eElement.getAttribute("name"), eElement.getAttribute("grpID"), eElement.getAttribute("uri"),
	    					 eElement.getAttribute("orgID"), eElement.getAttribute("subID"));

	    			//check to see if this is default
	    			if(org.getName().contentEquals("default")){
	    				
	    				org = new OrgConfig("default", "", TestConfigCustom.getInstance().getServerURL(),"","");
	    			}
	    			
	    			//add the organization to the list of organizations
	    			listOrgs.add(org);
	    			
	    		}
	    	}
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        
	        log.info("INFO: Number of Organizations " + listOrgs.size());
			return listOrgs;
		}
	

	
}
