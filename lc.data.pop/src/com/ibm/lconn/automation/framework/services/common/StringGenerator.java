package com.ibm.lconn.automation.framework.services.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

public class StringGenerator {
	
	private static StringGenerator instance;
	private Map<String, String> usedStrings;
	
	private static final String DATA_PATH = "src/resources/data.properties";
	private static final Properties appProperties = IOReader.loadExternalProperties(DATA_PATH);
	
	private StringGenerator(){
		usedStrings = new HashMap<String, String>();
	}
	
	public static StringGenerator getInstance() {
		if(instance == null){
			instance = new StringGenerator();
		}
		return instance;
	}

	public static String randomLorem1Sentence() {
		boolean sc = false;
		if(appProperties != null) {
			sc = appProperties.getProperty("special_characters", "false").equals("true");
		}
		StringGenerator sg = StringGenerator.getInstance();
		return sg.decorateSCAll(StringConstants.LOREM_1[RandomUtils.nextInt(StringConstants.LOREM_1.length-1)], sc);
	}

	public static String randomLorem2Sentence() {
		boolean sc = false;
		if(appProperties != null) {
			sc = appProperties.getProperty("special_characters", "false").equals("true");
		}
		StringGenerator sg = StringGenerator.getInstance();
		return sg.decorateSCAll(StringConstants.LOREM_2[RandomUtils.nextInt(StringConstants.LOREM_2.length-1)], sc);
	}

	public static String randomSentence(int numWords) {
		boolean sc = false;
		if(appProperties != null) {
			sc = appProperties.getProperty("special_characters", "false").equals("true");
		}
		StringGenerator sg = StringGenerator.getInstance();
		
		String sentence = "";
		
		for(int i = 0; i < numWords; i++) {
			sentence += " " + RandomStringUtils.randomAlphabetic(2 + RandomUtils.nextInt(6));
		}
		
		return sg.decorateSCAll(sentence, sc);
	}

	public static String randomParagraph(int numSentences) {
		String paragraph = "";
		
		for(int i = 0; i < numSentences; i++) {
			paragraph += " " + randomSentence(5 + RandomUtils.nextInt(5)) + RandomStringUtils.random(1, ".,!?");
		}
		
		return paragraph + "\n\n";
	}
	
	/**
	 * Generates string out of characters in the list
	 * @param length
	 * @param list
	 * @return
	 */
	public static String generateSCs(int length, String[] list){
		String word = "";
		
		for(int i = 0; i < length; i++){
			word += list[RandomUtils.nextInt(list.length)];
		}
		
		return word;
	}
	
	/**
	 * 
	 * @param charNums number of characters in the word
	 * @return string consists of only special characters
	 */
	public static String generateOnlySCs(int length) {
		
		return generateSCs(length, StringConstants.SCFULLLIST);
	}
	
	/**
	 * 
	 * @param charNums number of characters in the word
	 * @return string consists of only special characters valid for files upload
	 */
	public static String generateSCFilesValid(int length) {
		
		return generateSCs(length, StringConstants.SCFILESVALIDLIST);
	}
	
	/**
	 * 
	 * @param charNums number of characters in the word
	 * @return string consists of only special characters valid for files upload
	 */
	public static String generateSCFilesInvalid(int length) {
		
		return generateSCs(length, StringConstants.SCFILESINVALIDLIST);
	}
	
	/**
	 * 
	 * @param charNums number of characters in the word
	 * @return string consists of only special characters valid for wikis upload
	 */
	public static String generateSCWikisValid(int length) {
		
		return generateSCs(length, StringConstants.SCWIKISVALIDLIST);
	}
	
	/**
	 * 
	 * @param charNums number of characters in the word
	 * @return string consists of only special characters valid for wikis upload
	 */
	public static String generateSCWikisInvalid(int length) {
		
		return generateSCs(length, StringConstants.SCWIKISINVALIDLIST);
	}
	
	/**
	 * 
	 * @return array of three random strings with a potential problematic character sequence in the beginning, middle, and end
	 */
	public static String[] generateSCSequence() {
		String word1 = RandomStringUtils.randomAlphanumeric(2 + RandomUtils.nextInt(6));
		String seq = StringConstants.SCCOMBOLIST[RandomUtils.nextInt(StringConstants.SCCOMBOLIST.length)];
		String word2 = RandomStringUtils.randomAlphanumeric(2 + RandomUtils.nextInt(6));
		
		String[] word = {seq+word1+word2, word1+seq+word2, word1+word2+seq};
		
		return word;
	}
	
	/**
	 * 
	 * @param seq sequence to insert between words
	 * @return array of three random strings with seq in the beginning, middle, and end
	 */
	public static String[] generateSCSequence(String seq) {
		String word1 = RandomStringUtils.randomAlphanumeric(2 + RandomUtils.nextInt(6));
		String word2 = RandomStringUtils.randomAlphanumeric(2 + RandomUtils.nextInt(6));
		String[] word = {seq+word1+word2, word1+seq+word2, word1+word2+seq};
		
		return word;
	}
	
	/**
	 * 
	 * @param charNums number of characters in the word
	 * @return random string with at least one special character 
	 */
	public static String randomStringWithSC(int charNums) {
		String word = "";
		boolean hasSC = false;
		
		for(int i = 0; i < charNums; i++){
			int c = RandomUtils.nextInt(2);
			if(c == 0)
				word += RandomStringUtils.randomAlphanumeric(1);
			else {
				word += StringConstants.SCFULLLIST[RandomUtils.nextInt(StringConstants.SCFULLLIST.length)];
				hasSC = true;
			}
		}
		
		if(!hasSC) {
			char[] wordChars = word.toCharArray();
			wordChars[RandomUtils.nextInt(charNums)] = StringConstants.SCFULLLIST[RandomUtils.nextInt(StringConstants.SCFULLLIST.length)].toCharArray()[0];
			word = String.valueOf(wordChars);
		}
		
		return word;
	}
	
	public static String appendSC(String word, boolean append) {
		if(append){
			word += generateOnlySCs(10);
		}
		return word;
	}
	
	public static String prependSC(String word, boolean prepend) {
		if(prepend) {
			word = generateOnlySCs(10) + word;
		}
		return word;
	}
	
	public String decorateSCString(String word, String[] scs, boolean decorate, int maxLength) {
		if(decorate) {
			if(usedStrings.get(word) != null){
				return usedStrings.get(word);
			}
			String newWord = "";
			int wordLength = word.length();
			int index = 0;
			int newWordLength = 0;
			
			while(index < wordLength && newWordLength < maxLength) {
				int c = RandomUtils.nextInt(2);
				if(c == 0){
					newWord += word.charAt(index);
					index++;
				}
				else {
					newWord += scs[RandomUtils.nextInt(scs.length)];
				}
				newWordLength++;
			}
			newWord = newWord.trim();
			usedStrings.put(word, newWord);
			word = newWord;
		}
		return word;
	}
	
	public String decorateSCAll(String word, boolean decorate){
		return decorateSCString(word, StringConstants.SCFULLLIST, decorate, word.length());
	}
	
	public String decorateSCWiki(String word, boolean decorate){
		return decorateSCString(word, StringConstants.SCWIKISVALIDLIST, decorate, word.length());
	}
	
	public String decorateSCFile(String word, boolean decorate){
		return decorateSCString(word, StringConstants.SCFILESVALIDLIST, decorate, word.length());
	}

}
