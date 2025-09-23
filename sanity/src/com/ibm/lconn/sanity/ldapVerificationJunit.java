package com.ibm.lconn.sanity;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.ibm.json.java.JSONObject;

public class ldapVerificationJunit {
	
	@Test
	public void validateLdapConnection() throws FileNotFoundException, IOException{
		//LdapService tempLdap = new LdapService("testLdap.properties");
		
		JSONObject json = JSONObject.parse(new BufferedReader(new FileReader("C:\\Users\\IBM_ADMIN\\workspace\\sn.auto\\lwp\\sanity\\samples\\ldapConf.json")));
		
		String LDAP_SERVER = (String)((Map)json.get("params")).get("LDAP_SERVER");
		String LDAP_PORT = (String)((Map)json.get("params")).get("LDAP_PORT");
		String LDAP_USER = (String)((Map)json.get("params")).get("LDAP_USER");
		String LDAP_PASSWORD = (String)((Map)json.get("params")).get("LDAP_PASSWORD");
		String LDAP_BASE = (String)((Map)json.get("params")).get("LDAP_BASE");
		String LDAP_FILTER = (String)((Map)json.get("params")).get("LDAP_FILTER");
		
		LdapService tempLdap = new LdapService(LDAP_SERVER,LDAP_PORT,LDAP_USER,LDAP_PASSWORD,LDAP_BASE,LDAP_FILTER);
		
		boolean canConnect = tempLdap.setupContext();
		assertTrue("able to connect to lDap",canConnect);
	}

	@Test
	public void ldapIsPopulated() throws FileNotFoundException, IOException {
		//LdapService tempLdap = new LdapService("testLdap.properties");
		
		JSONObject json = JSONObject.parse(new BufferedReader(new FileReader("C:\\Users\\IBM_ADMIN\\workspace\\sn.auto\\lwp\\sanity\\samples\\ldapConf.json")));
		
		String LDAP_SERVER = (String)((Map)json.get("params")).get("LDAP_SERVER");
		String LDAP_PORT = (String)((Map)json.get("params")).get("LDAP_PORT");
		String LDAP_USER = (String)((Map)json.get("params")).get("LDAP_USER");
		String LDAP_PASSWORD = (String)((Map)json.get("params")).get("LDAP_PASSWORD");
		String LDAP_BASE = (String)((Map)json.get("params")).get("LDAP_BASE");
		String LDAP_FILTER = (String)((Map)json.get("params")).get("LDAP_FILTER");
		
		LdapService tempLdap = new LdapService(LDAP_SERVER,LDAP_PORT,LDAP_USER,LDAP_PASSWORD,LDAP_BASE,LDAP_FILTER);
		
		tempLdap.setupContext();
		int[] hasPopulatedValues = tempLdap.verifyPopulatedResults();
		assertTrue("values are populated",hasPopulatedValues[0]==1);
	}

}
