package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertEquals;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.DateFilter;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtQueryWithHighlightParameterTestWithDate extends ActivityStreamSearchTest{

	
	private static final String REQUEST_TO_SEND = "/@public/@all/@all?";
	private static String searchUrlHighlightTrue = "query=text_to_search&highlight=true";
	private static String searchUrlHighlightFalse = "query=text_to_search&highlight=false";
	private static String searchUrlWithoutHighlight = "query=text_to_search";
	private static String elementToSearch = ".title";
	private static String requestToExecute;
	private static String splitString = " ";
	private static String joinString = " ";
	ArrayList<String> entriesListTitle = new ArrayList<String>(); 
	private static String wordForQuery = "Manager";
	private static String sentenceForQuery = "Everybody First Team Tiger";
	
	private static String accentWordForQueryUnicode = "\u00e9\u0071\u0075\u0069\u0070\u0065\u0073";
	private static String accentWordToSearch = "&eacute;quipes";
	

	

	@Test
	public void checkHighlightParameterTrueSearchForSingleWord()
			throws Exception {
		checkSingleHighlightedWord(wordForQuery, searchUrlHighlightTrue, true);
	}

	@Test
	public void checkHighlightParameterFalseSearchForSingleWord()
			throws Exception {
		checkSingleHighlightedWord(wordForQuery, searchUrlHighlightFalse, false);
	}

	@Test
	public void checkNoHighlightParameterSearchForSingleWord() throws Exception {
		checkSingleHighlightedWord(wordForQuery, searchUrlWithoutHighlight,
				false);
	}

	@Test
	public void checkHighlightParameterTrueSearchForSentence() throws Exception {
		checkHighlightedWordsInSentence(sentenceForQuery,
				searchUrlHighlightTrue, true);
	}

	@Test
	public void checkHighlightParameterFalseSearchForSentence()
			throws Exception {
		checkHighlightedWordsInSentence(sentenceForQuery,
				searchUrlHighlightFalse, false);
	}

	@Test
	public void checkNoHighlightParameterSearchForSentence() throws Exception {
		checkHighlightedWordsInSentence(sentenceForQuery,
				searchUrlWithoutHighlight, false);
	}

	
	@Test
	public void checkHighlightParameterTrueSearchForSingleAccentWordUnicode()
			throws Exception {
		checkSingleHighlightedAccentWord(accentWordForQueryUnicode,
				accentWordToSearch, searchUrlHighlightTrue, true);
	}

	@Test
	public void checkHighlightParameterFalseSearchForSingleAccentWordUnicode()
			throws Exception {
		checkSingleHighlightedAccentWord(accentWordForQueryUnicode,
				accentWordToSearch, searchUrlHighlightFalse, false);
	}

	@Test
	public void checkNoHighlightParameterSearchForSingleAccentWordUnicode()
			throws Exception {
		checkSingleHighlightedAccentWord(accentWordForQueryUnicode,
				accentWordToSearch, searchUrlWithoutHighlight, false);
	}

	

	// ############################################Working methods##########################################################
	

	private boolean searchSentenceInEntry(
			ArrayList<String> entriesListTitleToSearch,
			String highlightedSentenceToSearch, String regularSentenceToSearch) {
		
		boolean highlightWordFound = false;
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
