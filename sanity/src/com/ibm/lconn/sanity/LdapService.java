package com.ibm.lconn.sanity;

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

public class LdapService extends LdapBase{

	public LdapService(String propFileName) {
		super(propFileName);
	}
	
	public LdapService(String server, String port, String user, String password, String base, String filter) {
		super(server, port, user, password, base, filter);
	}
	
	public NamingEnumeration<SearchResult> search(String searchBy, String name) {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		constraints.setReturningAttributes(null);
		try {
			return ldapContext.search(base, searchBy + "=" + name, constraints);
		} catch (NamingException e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	//return true if specified base and filter return any results
	public int[] verifyPopulatedResults() {
		//CONSIDER: call setupContext() here so we know it is called before anything else executes
		//currently, I have it called before any other method gets called each time
		//I use LdapService... might be sloppy for future users/people working with this code
		
		//{ 0 for false and 1 for true , # of entries returned }
		int[] response = {0,0};
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		constraints.setReturningAttributes(null);
		try {
			//base and filter are defined in LdapBase, which is being extended
			//base and filter are originally defined in the properties file (testLdap.properties)
			
			//should I use .next() in the following call?
			//using .next() shows an element of the enumeration
			//whereas you would otherwise be getting a toString of an enum, which I doubt would ever be null
			//i.e. this: com.sun.jndi.ldap.LdapSearchEnumeration@20e420e4 instead of an element
			if(ldapContext.search(base, filter, constraints).next()!=null){
				NamingEnumeration<SearchResult> ldap = ldapContext.search(base, filter, constraints);
				int k = 0;
				while(ldap.hasMore()){
					k++;
					ldap.next();
				}
				//System.out.println("number of entries is at least " + k);
				
				response[0] = 1;
				response[1] = k;
				return response;
			}

			//reaching this else means nothing (null) was returned
			//with the current base and filter configuration
			else{
				return response;
			}
		//reaching this catch means something went wrong with the ldap
		//and should return false for the verification
		} catch (NamingException e) {
			//e.printStackTrace();
			return response;
		}
		catch (NullPointerException e) {
			//e.printStackTrace();
			return response;
		}
	}
	
	//the rest of the functions were already here
	//leaving them for now in case they can be used later
	
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
			//e.printStackTrace();
			return null;
		}
	}
	
	public void deleteUser(String uid) {
		try {
			ldapContext.destroySubcontext(getDN(uid));
		} catch (NamingException e) {
			//e.printStackTrace();
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
			//e.printStackTrace();
		}
	}
	
	private String getDN(String uid) {
		return "uid="+uid+"," + base;
	}
	
}
