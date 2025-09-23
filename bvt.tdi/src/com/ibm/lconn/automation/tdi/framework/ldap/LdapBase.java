package com.ibm.lconn.automation.tdi.framework.ldap;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.testng.Assert;

import com.ibm.lconn.automation.tdi.framework.Util.PropertiesInstance;

public class LdapBase {
	
	protected LdapContext ldapContext;
	protected String base;
	
	public LdapBase(String propFileName) {
		Properties prop = PropertiesInstance.getInstance(propFileName);
		
		setupContext(prop);
	}
	
	private void setupContext(Properties prop) {
		String ldapURL = prop.getProperty("LDAP_SERVER");
		String port = prop.getProperty("LDAP_PORT");
		String bindDN = prop.getProperty("LDAP_USER");
		String bindPW = prop.getProperty("LDAP_PASSWORD");
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://"+ ldapURL + ":" + port);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");	
		env.put(Context.SECURITY_PRINCIPAL, bindDN); // bind user login
		env.put(Context.SECURITY_CREDENTIALS, bindPW); //bind user password
		env.put(Context.REFERRAL, "follow");
		
		try {
			ldapContext = new InitialLdapContext(env, null);
		} catch (NamingException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		base = prop.getProperty("LDAP_BASE");
		if(base == null)
			throw new NullPointerException("Base is not set");
	}

}
