package com.ibm.lconn.automation.tdi.framework.ldap;

import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.testng.Assert;

public class LdapService extends LdapBase{

	public LdapService(String propFileName) {
		super(propFileName);
	}
	
	public NamingEnumeration<SearchResult> search(String searchBy, String name) {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		constraints.setReturningAttributes(null);
		try {
			return ldapContext.search(base, searchBy + "=" + name, constraints);
		} catch (NamingException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
			return null;
		}
//		while(results.hasMore()) {
//			SearchResult sr = results.next();
//			Attributes attrs = sr.getAttributes();
//			Attribute attr = attrs.get("cn");
//		    System.out.print(attr.get() + ": ");
//		}
	}
	
	public DirContext addUser(String uid, List<Attribute> attr) {
		
		// Create a container set of attributes
		Attributes container = new BasicAttributes();
		
		// Create the objectclass to add
        Attribute objClasses = new BasicAttribute("objectClass");
        objClasses.add("top");
        objClasses.add("person");
        objClasses.add("organizationalPerson");
        objClasses.add("inetOrgPerson");
        objClasses.add("ePerson");
        
        Attribute id = new BasicAttribute("uid", uid);
        
        container.put(objClasses);
        container.put(id);
        for(Attribute att: attr){
        	container.put(att);
        }
        
        DirContext user;
		try {
			user = ldapContext.createSubcontext(getDN(uid), container);
			return user;
		} catch (NamingException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
			return null;
		}
	}
	
	public void deleteUser(String uid) {
		try {
			ldapContext.destroySubcontext(getDN(uid));
		} catch (NamingException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	public void modifyUser(String uid, List<Attribute> attr) {
		Attributes attrs = new BasicAttributes();
		for(Attribute att: attr){
			attrs.put(att);
		}
		try {
			ldapContext.modifyAttributes(getDN(uid), DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	private String getDN(String uid) {
		return "uid="+uid+"," + base;
	}
	
}
