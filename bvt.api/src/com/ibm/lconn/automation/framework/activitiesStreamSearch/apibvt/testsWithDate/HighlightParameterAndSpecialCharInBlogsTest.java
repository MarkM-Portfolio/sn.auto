package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertEquals;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.DateFilter;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class HighlightParameterAndSpecialCharInBlogsTest extends ActivityStreamSearchTest{

	
	private static final String REQUEST_TO_SEND = "/@public/@all/@all?";
	private static String searchUrlHighlightTrue = "query=text_to_search&highlight=true";
	private static String searchUrlHighlightFalse = "query=text_to_search&highlight=false";
	private static String searchUrlWithoutHighlight = "query=text_to_search";
	private static String elementToSearch = ".title";
	private static String requestToExecute;
	private static String splitString = " ";
	private static String joinString = " ";
	ArrayList<String> entriesListTitle = new ArrayList<String>(); 
	
	private static String specialcharactersQueryString = "one two&three";
	private static String specialCharactersTestWordsToSearch = "one two three";
	private static String specialCharactersHighlightedString = "<B>one</B> <B>two</B>&amp;<B>three</B>";
	private static String specialcharactersQueryString2 = "four five@six";
	private static String specialCharactersTestWordsToSearch2 = "four five six";
	private static String specialCharactersHighlightedString2 = "<B>four</B> <B>five</B>@<B>six</B>";
	private static String specialcharactersQueryString3 = "seven eight@nine";
	private static String specialCharactersTestWordsToSearch3 = "seven eight nine";
	private static String specialCharactersHighlightedString3 = "<B>seven</B> <B>eight</B>$<B>nine</B>";

	

	@Test
	public void checkHighlightParameterTrueSearchForStringWithSpecialCharcters()
			throws Exception {
		checkHighlightedSentenceWithSpecialCharacters(
				specialcharactersQueryString,
				specialCharactersHighlightedString,
				specialCharactersTestWordsToSearch, searchUrlHighlightTrue,
				true);
	}

	@Test
	public void checkHighlightParameterFalseSearchForStringWithSpecialCharcters()
			throws Exception {
		checkHighlightedSentenceWithSpecialCharacters(
				specialcharactersQueryString,
				specialCharactersHighlightedString,
				specialCharactersTestWordsToSearch, searchUrlHighlightFalse,
				false);
	}

	@Test
	public void checkNoHighlightParameterSearchForStringWithSpecialCharcters()
			throws Exception {
		checkHighlightedSentenceWithSpecialCharacters(
				specialcharactersQueryString,
				specialCharactersHighlightedString,
				specialCharactersTestWordsToSearch, searchUrlWithoutHighlight,
				false);
	}

	@Test
	public void checkHighlightParameterTrueSearchForStringWithSpecialCharcters2()
			throws Exception {
		checkHighlightedSentenceWithSpecialCharacters(
				specialcharactersQueryString2,
				specialCharactersHighlightedString2,
				specialCharactersTestWordsToSearch2, searchUrlHighlightTrue,
				true);
	}

	@Test
	public void checkHighlightParameterFalseSearchForStringWithSpecialCharcters2()
			throws Exception {
		checkHighlightedSentenceWithSpecialCharacters(
				specialcharactersQueryString2,
				specialCharactersHighlightedString2,
				specialCharactersTestWordsToSearch2, searchUrlHighlightFalse,
				false);
	}

	@Test
	public void checkNoHighlightParameterSearchForStringWithSpecialCharcters2()
			throws Exception {
		checkHighlightedSentenceWithSpecialCharacters(
				specialcharactersQueryString2,
				specialCharactersHighlightedString2,
				specialCharactersTestWordsToSearch2, searchUrlWithoutHighlight,
				false);
	}

	@Test
	public void checkHighlightParameterTrueSearchForStringWithSpecialCharcters3()
			throws Exception {
		checkHighlightedSentenceWithSpecialCharacters(
				specialcharactersQueryString3,
				specialCharactersHighlightedString3,
				specialCharactersTestWordsToSearch3, searchUrlHighlightTrue,
				true);
	}

	@Test
	public void checkHighlightParameterFalseSearchForStringWithSpecialCharcters3()
			throws Exception {
		checkHighlightedSentenceWithSpecialCharacters(
				specialcharactersQueryString3,
				specialCharactersHighlightedString3,
				specialCharactersTestWordsToSearch3, searchUrlHighlightFalse,
				false);
	}

	@Test
	public void checkNoHighlightParameterSearchForStringWithSpecialCharcters3()
			throws Exception {
		checkHighlightedSentenceWithSpecialCharacters(
				specialcharactersQueryString3,
				specialCharactersHighlightedString3,
				specialCharactersTestWordsToSearch3, searchUrlWithoutHighlight,
				false);
	}

	// ############################################Working methods##########################################################
	

	private boolean searchSentenceInEntry(
			ArrayList<String> entriesListTitleToSearch,
			String highlightedSentenceToSearch, String regularSentenceToSearch) {
		
		boolean highlightWordFound=false;
		boolean allWordsFoundInEntry;

		String[] sentenceArray = regularSentenceToSearch.split(splitString);

		if (!entriesListTitleToSearch.isEmpty()) {
			for (int i = 0; i < entriesListTitleToSearch.size(); i++) {
				
				allWordsFoundInEntry = checkWordsInSentenceExistance(
						entriesListTitleToSearch.get(i), sentenceArray);
				if (allWordsFoundInEntry) {
					
					highlightWordFound = FVTUtilsWithDate.searchWordInEntry(
							entriesListTitleToSearch.get(i),
							highlightedSentenceToSearch);
					
				} 

			}
		} 
		return highlightWordFound;
	}

	public String createHighlightedSentence(String stringToConvert) {
		String[] newSentence = stringToConvert.split(splitString);
		String newHighlightedString;
		
		for (int i = 0; i < newSentence.length; i++) {
			newSentence[i] = newSentence[i].replace(newSentence[i], "<B>"
					+ newSentence[i] + "</B>");
			
		}
		newHighlightedString = StringUtils.join(newSentence, joinString);
		return newHighlightedString;
	}

	public String createSentenceForSearch(String stringToConvert) {
		String sentenceToSearch;
		String[] newSentence = stringToConvert.split(splitString);
		sentenceToSearch = StringUtils.join(newSentence, joinString);
		
		return sentenceToSearch;
	}

	public Boolean checkWordsInSentenceExistance(String entryToSearchIn,
			String[] wordsArray) {
		boolean wordFound = false;
		for (int i = 0; i < wordsArray.length; i++) {
			
			wordFound = FVTUtilsWithDate.searchWordInEntry(
					entryToSearchIn, wordsArray[i]);
			if (!wordFound) {
				return false;
			}
		}
		return wordFound;
	}

	private String buildSearchUrl(String searchString) {
		return urlPrefix + REQUEST_TO_SEND + searchString + "&count="
				+ PopStringConstantsAS.RECEIVED_PAGE_SIZE
				+ DateFilter.instance().getDateFilterParam();
	}

	public void checkSingleHighlightedWord(String wordForQuery,
			String searchString, Boolean shouldHighlightedWordBeFound)
			throws Exception {
		String highlightWordToSearch = "<B>" + wordForQuery + "</B>";
		entriesListTitle = getEntriesListFromRequest(searchString,
				wordForQuery, false);
		
		boolean isHighlightedFound = searchSentenceInEntry(entriesListTitle,
				highlightWordToSearch, wordForQuery);
		assertEquals(shouldHighlightedWordBeFound.booleanValue(),
				isHighlightedFound);
	}

	public void checkHighlightedWordsInSentence(String sentenceForQuery,
			String searchString, Boolean shouldHighlightedSentenceBeFound)
			throws Exception {
		String highlightedSentenceForQuery = createHighlightedSentence(sentenceForQuery);
		
		String sentenceToSearchInEntry = createSentenceForSearch(sentenceForQuery);
		
		entriesListTitle = getEntriesListFromRequest(searchString,
				sentenceForQuery, true);
		boolean isSentenceFoundInFirstRequest = searchSentenceInEntry(
				entriesListTitle, highlightedSentenceForQuery,
				sentenceToSearchInEntry);

		assertEquals(shouldHighlightedSentenceBeFound.booleanValue(),
				isSentenceFoundInFirstRequest);
	}

	public void checkSingleHighlightedAccentWord(String wordForQuery,
			String wordForSearch, String searchString,
			Boolean shouldHighlightedWordBeFound) throws Exception {
		String highlightWordToSearch = "<B>" + wordForSearch + "</B>";
		entriesListTitle = getEntriesListFromRequestForAccentWords(
				searchString, wordForQuery, true);
		
		boolean isHighlightedFound = searchSentenceInEntry(entriesListTitle,
				highlightWordToSearch, wordForSearch);
		assertEquals(shouldHighlightedWordBeFound.booleanValue(),
				isHighlightedFound);
	}

	public void checkHighlightedSentenceWithSpecialCharacters(
			String stringForQuery, String highlightedStringForSearch,
			String wordsToSearch, String searchString,
			Boolean shouldHighlightedWordBeFound) throws Exception {
		entriesListTitle = getEntriesListFromRequest(searchString,
				stringForQuery, true);
		
		boolean isHighlightedFound = searchSentenceInEntry(entriesListTitle,
				highlightedStringForSearch, wordsToSearch);
		assertEquals(shouldHighlightedWordBeFound.booleanValue(),
				isHighlightedFound);
	}

	public ArrayList<String> getEntriesListFromRequest(String queryUrl,
			String queryString, Boolean isEncode) throws Exception {
		ArrayList<String> responseList = new ArrayList<String>();
		requestToExecute = buildSearchUrl(queryUrl);
		if (isEncode) {
			requestToExecute = requestToExecute.replace("text_to_search",
					URLEncoder.encode(queryString, "UTF-8"));
		} else {
			requestToExecute = requestToExecute.replace("text_to_search",
					queryString);
		}
		
		responseList = FVTUtilsWithDate.getJsonResponseEntriesElementList(
				requestToExecute, elementToSearch);
		
		return responseList;
	}

	public ArrayList<String> getEntriesListFromRequestForAccentWords(
			String queryUrl, String queryString, Boolean isEncode)
			throws Exception {
		ArrayList<String> responseList = new ArrayList<String>();
		requestToExecute = buildSearchUrl(queryUrl);
		if (isEncode) {
			requestToExecute = requestToExecute.replace("text_to_search",
					URLEncoder.encode(queryString, "UTF-8"));
		} else {
			requestToExecute = requestToExecute.replace("text_to_search",
					queryString);
		}
		
		responseList = FVTUtilsWithDate
				.getJsonResponseEntriesElementListForAccentWords(
						requestToExecute, elementToSearch);
		
		return responseList;
	}

	

}
