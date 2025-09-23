/**
 * 
 */
package com.ibm.atmn.waffle.extensions.user;

import java.security.InvalidParameterException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class User.
 * 
 * @author Liam Walsh
 */
public class User {

	private static final Logger log = LoggerFactory.getLogger(User.class);

	// Model a Communities user
	/** The Email. */
	private String Email;

	/** The Password. */
	private String Password;

	/** The First name. */
	private String FirstName;

	/** The Last name. */
	private String LastName;

	/** The Uid. */
	private String Uid;

	/** The Display name. */
	private String DisplayName;

	/** Class hashcode */
	private int HashCode = 0;

	private HashMap<String, String> attributes;

	// User constructor
	/**
	 * Instantiates a new user.
	 * 
	 * @param ID
	 *            the unique id of the user
	 * @param Pword
	 *            the user password
	 * @param Email
	 *            the user email address
	 * @param FN
	 *            the user First name
	 * @param LN
	 *            the user Last name
	 * @param Lang
	 *            the user language
	 * @param DN
	 *            the user Display name
	 * @param ThId
	 *            the thread id which has locked the user
	 */
	public User(String ID, String Pword, String Email, String FN, String LN, String Lang, String DN, String ThId) {

		this.Uid = ID;
		this.Password = Pword;
		this.Email = Email;
		this.FirstName = FN;
		this.LastName = LN;
		this.DisplayName = DN;
		setAttribute("uid", this.Uid);
		setAttribute("password", this.Password);
		setAttribute("email", this.Email);
		setAttribute("first name", this.FirstName);
		setAttribute("last name", this.LastName);
		setAttribute("display name", this.DisplayName);

	}

	public User(HashMap<String, String> map) {

		this.attributes = map;
		this.Uid = getAttribute("uid");
		this.Password = getAttribute("password");
		this.Email = getAttribute("email");
		this.FirstName = getAttribute("first name");
		this.LastName = getAttribute("last name");
		this.DisplayName = getAttribute("display name");
	}

	public String getAttribute(String attribute) {

		String result = this.attributes.get(attribute);
		if (result == null) {
			log.error("The attribute '" + attribute + "' does not correspond to an attribute of the user. Check that the attribute corresponds to a header in your users CSV.");
			throw new InvalidParameterException("The attribute '" + attribute
					+ "' does not correspond to an attribute of the user. Check that the attribute corresponds to a header in your users CSV.");
		} else if (result == "") {
			log.warn("Request for attribute '" + attribute + "' is returning an empty string for user " + Uid + ". Verify that your users CSV is populated correctly.");
		}
		return result;
	}

	public String setAttribute(String attribute, String value) {

		return this.attributes.put(attribute, value);
	}

	// User methods
	/**
	 * Gets the email.
	 * 
	 * @return the email
	 */
	public String getEmail() {

		return Email;
	}

	/**
	 * Sets the email.
	 * 
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {

		Email = email;
	}

	/**
	 * Gets the first name.
	 * 
	 * @return the firstName
	 */
	public String getFirstName() {

		return FirstName;
	}

	/**
	 * Sets the first name.
	 * 
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {

		FirstName = firstName;
	}

	/**
	 * Gets the last name.
	 * 
	 * @return the lastName
	 */
	public String getLastName() {

		return LastName;
	}

	/**
	 * Sets the last name.
	 * 
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {

		LastName = lastName;
	}

	/**
	 * Gets the uid.
	 * 
	 * @return the uid
	 */
	public String getUid() {

		return Uid;
	}

	/**
	 * Sets the uid.
	 * 
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(String uid) {

		this.Uid = uid;
	}

	/**
	 * Gets the display name.
	 * 
	 * @return the displayName
	 */
	public String getDisplayName() {

		return DisplayName;
	}

	/**
	 * Sets the display name.
	 * 
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {

		DisplayName = displayName;
	}

	/**
	 * Gets the password.
	 * 
	 * @return the password
	 */
	public String getPassword() {

		return Password;
	}

	/**
	 * Sets the password.
	 * 
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {

		Password = password;
	}

	boolean isCheckOutToken(Object checkOutToken) {

		return this.HashCode == checkOutToken.hashCode();
	}

	void checkOut(Object checkOutToken) {

		this.HashCode = checkOutToken.hashCode();
	}

	public void checkIn() {

		this.HashCode = 0;
	}

	public boolean isCheckedOut() {

		return this.HashCode != 0;
	}

}
