package com.ibm.lconn.sanity;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class LdapBase {
	
	protected LdapContext ldapContext;
	protected String base;
	protected String filter;
	protected Properties property;
	
	public LdapBase(String propFileName) {
		Properties prop = PropertiesInstance.getInstance(propFileName);
		
		//OR, go to http://docs.oracle.com/javase/6/docs/api/java/util/Properties.html
		//and create a properties instance, then use setProperty(String key, String value)
		
		//removed the following line because I wanted to be able to
		//be able to create an instance without calling setupContext()
		//otherwise we don't know if there is something wrong with
		//the class or the method if it fails
		
		//setupContext(prop);
		property = prop;
	}
	
	//TO DO: build an overloaded constructor that can take strings for input values from the driver
	public LdapBase(String server, String port, String user, String password, String base, String filter) {
		Properties prop = new Properties();
		prop.setProperty("LDAP_SERVER",server);
		prop.setProperty("LDAP_PORT",port);
		prop.setProperty("LDAP_USER",user);
		prop.setProperty("LDAP_PASSWORD",password);
		prop.setProperty("LDAP_BASE",base);
		prop.setProperty("LDAP_FILTER",filter);
		property = prop;
	}
	
	//was void, changed to boolean
	//was private, made public
	//removed argument Properties prop
	public boolean setupContext(/*Properties prop*/) {
		Properties prop = property;
		boolean response = true;
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
			//e.printStackTrace();
			response = false;
			return response;
		}
		
		base = prop.getProperty("LDAP_BASE");
		if(base == null){
			//throw new NullPointerException("Base is not set");
		}
		filter = prop.getProperty("LDAP_FILTER");
		if(filter == null){
			//throw new NullPointerException("Filter is not set");
		}
		return response;
	}

}
