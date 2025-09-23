package com.ibm.lconn.sanity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import com.ibm.json.java.JSONObject;



public class ldapVerification {
	
	//default location of properties file used for testing
	//could be tdi or websphere location
	static String defaultFilePath;
	
	public static void main(String [] args) throws IOException
	{
		
		//when the jar runs, it expects an argument for a file path
		//the python wrapper will always send a path, whether it is
		//the default path or one given by the user
		//if the jar is run on it's own, the user must provide a path
		if((!args[args.length-1].equals("validateLdapConnection"))&&(!args[args.length-1].equals("ldapIsPopulated"))){
			
			defaultFilePath = args[args.length-1];
			if(args.length==1){
				//a file path is provided, but no test is specified
				//run all of the tests!
				validateLdapConnection(defaultFilePath);
				ldapIsPopulated(defaultFilePath);
			}
			else{
				for(int i=0;i<args.length-1;i++){
					if(args[i].equals("validateLdapConnection")){
						//both a file path and a test are specified
						validateLdapConnection(defaultFilePath);
					}
					else if(args[i].equals("ldapIsPopulated")){
						//both a file path and a test are specified
						ldapIsPopulated(defaultFilePath);
					}
						
				}
			}
		}	
	}
		
	
	
	//checks for a connection given credentials
	public static void validateLdapConnection() throws IOException{
		validateLdapConnection(defaultFilePath);
	}
	
	public static void validateLdapConnection(String filePath) throws IOException{
		
		JSONObject json = JSONObject.parse(new BufferedReader(new FileReader(filePath)));
		
		String LDAP_SERVER = (String)((Map<String,String>)json.get("params")).get("ldap.server");
		String LDAP_PORT = (String)((Map)json.get("params")).get("ldap.port");
		String LDAP_USER = (String)((Map)json.get("params")).get("ldap.user");
		String LDAP_PASSWORD = (String)((Map)json.get("params")).get("ldap.password");
		String LDAP_BASE = (String)((Map)json.get("params")).get("ldap.base");
		String LDAP_FILTER = (String)((Map)json.get("params")).get("ldap.filter");
		
		
		LdapService tempLdap = new LdapService(LDAP_SERVER,LDAP_PORT,LDAP_USER,LDAP_PASSWORD,LDAP_BASE,LDAP_FILTER);
		boolean canConnect = tempLdap.setupContext();
		if(canConnect){
			System.out.println("10");
		}
		else{
			System.out.println("11");
		}
	}

	//checks to see if anything is present in the connected ldap
	public static void ldapIsPopulated() throws IOException {
		ldapIsPopulated(defaultFilePath);
	}
	
	public static void ldapIsPopulated(String filePath) throws IOException {
		JSONObject json = JSONObject.parse(new BufferedReader(new FileReader(filePath)));
		
		String LDAP_SERVER = (String)((Map)json.get("params")).get("ldap.server");
		String LDAP_PORT = (String)((Map)json.get("params")).get("ldap.port");
		String LDAP_USER = (String)((Map)json.get("params")).get("ldap.user");
		String LDAP_PASSWORD = (String)((Map)json.get("params")).get("ldap.password");
		String LDAP_BASE = (String)((Map)json.get("params")).get("ldap.base");
		String LDAP_FILTER = (String)((Map)json.get("params")).get("ldap.filter");
		
		LdapService tempLdap = new LdapService(LDAP_SERVER,LDAP_PORT,LDAP_USER,LDAP_PASSWORD,LDAP_BASE,LDAP_FILTER);
		tempLdap.setupContext();
		int[] hasPopulatedValues = tempLdap.verifyPopulatedResults();
		if(hasPopulatedValues[0]==1){
			System.out.println("20"+ hasPopulatedValues[1]);
		}
		else{
			System.out.println("21");
		}
	}

}
