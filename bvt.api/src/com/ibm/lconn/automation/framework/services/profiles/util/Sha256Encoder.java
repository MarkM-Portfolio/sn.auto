/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.automation.framework.services.profiles.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

public class Sha256Encoder {
	private static final String CLASSNAME = Sha256Encoder.class.getName();

	private static final Logger LOGGER = Logger.getLogger(CLASSNAME);

	public static final String CHARSET_UTF8 = "UTF-8";

	/**
	 * @param input
	 *            String to be trimmed, lower-cased, and hashed. See comments for method hashLowercaseString(String input, String charset,
	 *            boolean trim) In this method, the charset is assumed to be UTF-8 The string is lower-cased (in Locale.ENLGISH)
	 * @param trim
	 *            Whether to trim leading & trailing whitespace true - the input string is trimmed; false - not trimmed
	 * @return
	 */
	public static String hashLowercaseStringUTF8(final String input, final boolean trim) {
		boolean isDebug = LOGGER.isLoggable(Level.FINER);
		final String method = "hashLowercaseStringUTF8";
		if (isDebug) {
			LOGGER.entering(CLASSNAME, method, "input: " + input);
		}
		//
		String rtn = hashLowercaseString(input, CHARSET_UTF8, trim);
		//
		if (isDebug) {
			LOGGER.exiting(CLASSNAME, method, rtn);
		}
		return rtn;
	}

	/**
	 * @param input
	 *            String to be trimmed, lower-cased, and hashed.
	 * @param charset
	 *            used specified char set eg UTF-8
	 * @param trim
	 *            Whether to trim leading & trailing whitespace true - the input string is trimmed; false - not trimmed The input string is
	 *            lower-cased (in Locale.ENLGISH) Then a byte[] is extracted from the string in the indicated charset This byte[] is hashed
	 *            by a SHA-256 MessageDigest. The first 16 bytes (128 bits) of the resulting hash value is hex encoded
	 * 
	 * @return the resulting value
	 */
	public static String hashLowercaseString(final String input, final String charset, final boolean trim) {
		final String method = "hashLowercaseString";
		final boolean isDebug = LOGGER.isLoggable(Level.FINER);
		if (isDebug) {
			LOGGER.entering(CLASSNAME, method, "input: " + input);
		}
		//
		String rtn = commonHash(input, charset, trim, true, isDebug); // lower case the string
		//
		if (isDebug) {
			LOGGER.exiting(CLASSNAME, method, rtn);
		}
		return rtn;
	}

	/**
	 * @param input
	 *            String to be trimmed and hashed.
	 * @param charset
	 *            used specified char set eg UTF-8
	 * @param trim
	 *            Whether to trim leading & trailing whitespace true - the input string is trimmed; false - not trimmed
	 * @param lowerCase
	 *            Whether to lower-case the input string (in Locale.ENLGISH) Then a byte[] is extracted from the string in the indicated
	 *            charset This byte[] is hashed by a SHA-256 MessageDigest. The first 16 bytes (128 bits) of the resulting hash value is hex
	 *            encoded
	 * @return the resulting value
	 */
	public static String hashString(final String input, final String charset, final boolean trim, final boolean lowerCase) {
		final String method = "hashString";
		final boolean isDebug = LOGGER.isLoggable(Level.FINER);
		if (isDebug) {
			LOGGER.entering(CLASSNAME, method, "input: " + input);
		}
		//
		String rtn = commonHash(input, charset, trim, lowerCase, isDebug); // do not lower case the string
		//
		if (isDebug) {
			LOGGER.exiting(CLASSNAME, method, rtn);
		}
		return rtn;
	}

	private static String commonHash(String input, String charset, boolean trim, boolean toLower, final boolean isDebug) {
		String str = cleanInput(input, trim, toLower);
		String rtn = hash(str, charset, isDebug);
		return rtn;
	}

	private static String cleanInput(final String input, final boolean trim, final boolean toLower) {
		String retVal = "";
		if (StringUtils.isNotEmpty(input)) {
			String str = input;
			if (trim) str = input.trim();
			if (toLower) str = str.toLowerCase(Locale.ENGLISH);
			retVal = str;
		}
		return retVal;
	}

	// assume public guard classes filter out empty strings
	private static String hash(String input, String charset, boolean isDebug) {
		String rtn = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			// retrieve the byte array from input with UTF-8 encoding.
			byte[] byteArray = input.getBytes(charset);
			byteArray = digest.digest(byteArray);
			// take the first 16 bytes
			byteArray = Arrays.copyOf(byteArray, 16);
			// hex encode this hash - or roll our own encoder.
			char[] apacheHex = org.apache.commons.codec.binary.Hex.encodeHex(byteArray);
			// create String representation.
			rtn = new String(apacheHex);
		}
		catch (NoSuchAlgorithmException nsae) {
			LOGGER.log(Level.SEVERE, "Sha256Encoder detected NoSuchAlgorithmException");
			rtn = null;
		}
		catch (UnsupportedEncodingException uee) {
			LOGGER.log(Level.SEVERE, "Sha256Encoder detected NoSuchAlgorithmException");
			rtn = null;
		}
		return rtn;
	}
}
