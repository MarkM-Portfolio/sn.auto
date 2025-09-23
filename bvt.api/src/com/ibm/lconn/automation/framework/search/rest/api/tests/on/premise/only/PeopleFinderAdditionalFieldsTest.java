package com.ibm.lconn.automation.framework.search.rest.api.tests.on.premise.only;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.json.java.JSONArray;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.ProfileCreator;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.data.PeopleFinderAdditionalField;
import com.ibm.lconn.automation.framework.services.search.data.PeopleFinderAdditionalField.AdditionalFieldConfidence;
import com.ibm.lconn.automation.framework.services.search.data.PeopleFinderAdditionalField.AdditionalFieldValues;
import com.ibm.lconn.automation.framework.services.search.request.PeopleFinderRequest;
import com.ibm.lconn.automation.framework.services.search.response.PeopleFinderResponse;
import com.ibm.lconn.automation.framework.services.search.response.PeopleFinderResponse.Person;
import com.ibm.lconn.automation.framework.services.search.service.PeopleFinderService;

/**
 * 
 * @author reuven
 * 
 */
public class PeopleFinderAdditionalFieldsTest {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance()
			.getPeopleFinderLogger();

	private PeopleFinderService peopleFinderService;

	private RestAPIUser searchUser;

	boolean isGivenNamesAvalable = false;

	boolean isPoneNumberAvalable = false;

	boolean isAddressAvalable = false;

	boolean isTagsAvailable = false;

	@BeforeMethod
	public void setUp() throws Exception {

		LOGGER.fine("Start Initializing RestAPISearchTest setUp");
		searchUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry searchServiceEntry = searchUser.getService("search");
		assert (searchServiceEntry != null);
		peopleFinderService = new PeopleFinderService(
				searchUser.getAbderaClient(), searchServiceEntry);

	}

	@Test
	/**
	 * 112020
	 * Check that default fields received and additional field received only of match query
	 */
	public void testCheckPeopleLists() throws Exception {
		LOGGER.fine("Test PeopleFinderAdditionalFieldsTest#testCheckPeopleLists");

		// default request
		String query = SearchRestAPIUtils.getEmailDomain(searchUser
				.getProfData().getEmail());
		PeopleFinderRequest requestDefault = new PeopleFinderRequest(query);
		requestDefault.setPageSize(500);
		PeopleFinderResponse peopleFinderResponse = typeAhead(requestDefault);

		List<Person> persons = peopleFinderResponse.getPersons();
		// check field that should be received by default request ( without
		// additionalFields )
		assertTrue("No people found. query: " + query, persons.size() > 0);

		for (Person person : persons) {
			boolean verifyJobResponsibilityField = false;
			if (person.getEmail()
					.equals(ProfileLoader.getProfile(0).getEmail())) {
				verifyJobResponsibilityField = true;
			}
			checkDefaultFields(person, verifyJobResponsibilityField);
			checkPossibleFields(query, person);

			// tags
			String[] tags = person.getTag();
			if (tags != null) {
				for (String tag : tags) {
					assertTrue(tag.contains(query));
				}
			}
		}
	}

	@Test
	/**
	 * 112020
	 * Search for populated tag and check that the found person has all of the default fields, and tags match should include the specific tag
	 */
	public void testCheckAdditionaField() throws Exception {
		LOGGER.fine("Test PeopleFinderAdditionalFieldsTest#testCheckAdditionaField");

		// default request
		String query = SearchRestAPIUtils.getExecId(Purpose.PEOPLE_FINDER);
		PeopleFinderRequest requestDefault = new PeopleFinderRequest(query);
		// requestDefault.setPageSize(10000); // workaround for defect
		// create additional field
		PeopleFinderAdditionalField additionalFiled = new PeopleFinderAdditionalField();
		Set<AdditionalFieldValues> fieldValues1 = new HashSet<AdditionalFieldValues>();
		fieldValues1.add(AdditionalFieldValues.tag);
		additionalFiled.addFields(AdditionalFieldConfidence.medium,
				fieldValues1);
		requestDefault.setAdditionalFields(additionalFiled);

		PeopleFinderResponse peopleFinderResponse = typeAhead(requestDefault);

		List<Person> persons = peopleFinderResponse.getPersons();
		// check field that should be received by default request ( without
		// additionalFields )
		assertTrue("No people found. query: " + query, persons.size() > 0);
		assertTrue("Only one person should be found by its tag",
				persons.size() == 1);

		for (Person person : persons) {
			checkDefaultFields(person, true);
			checkTags(person);
		}

	}

	@Test
	/**
	 * 112020
	 * Search for populated tag and check that the found person has all of the default fields, and tags match should include the specific tag
	 */
	public void testAdditionaFieldCheckGivenNameAndPhoneNumber()
			throws UnsupportedEncodingException {
		LOGGER.fine("Test PeopleFinderAdditionalFieldsTest#testAdditionaFieldCheckGivenNameAndPhoneNumber");
		String query = ProfileCreator.getEmail('A');
		PeopleFinderRequest requestDefault = new PeopleFinderRequest(query);
		// create additional field
		PeopleFinderAdditionalField additionalFiled = new PeopleFinderAdditionalField();
		Set<AdditionalFieldValues> fieldValues = new HashSet<AdditionalFieldValues>();
		fieldValues.add(AdditionalFieldValues.givenNames);
		fieldValues.add(AdditionalFieldValues.workPhone);
		additionalFiled
				.addFields(AdditionalFieldConfidence.medium, fieldValues);
		requestDefault.setAdditionalFields(additionalFiled);

		PeopleFinderResponse peopleFinderResponse = typeAhead(requestDefault);
		List<Person> persons = peopleFinderResponse.getPersons();
		assertTrue("The response should receive one person only. query: "
				+ query, persons.size() == 1);
		assertTrue("The workPhone was not received ", persons.get(0)
				.getWorkPhone() != null);
		assertTrue("The givenNames was not received ", persons.get(0)
				.getGivenNames() != null);
	}

	@Test
	/**
	 * 112020
	 * Search for populated tag and check that the found person has all of the default fields, and tags match should include the specific tag
	 */
	public void testAdditionaFieldCheckJobResopnsibilityAndAddress()
			throws UnsupportedEncodingException {
		LOGGER.fine("Test PeopleFinderAdditionalFieldsTest#testAdditionaFieldCheckJobResopnsibilityAndAddress");
		String query = ProfileCreator.getEmail('A');
		PeopleFinderRequest requestDefault = new PeopleFinderRequest(query);
		// create additional field
		PeopleFinderAdditionalField additionalFiled = new PeopleFinderAdditionalField();
		Set<AdditionalFieldValues> fieldValues = new HashSet<AdditionalFieldValues>();
		fieldValues.add(AdditionalFieldValues.jobResponsibility);
		fieldValues.add(AdditionalFieldValues.country);
		additionalFiled
				.addFields(AdditionalFieldConfidence.medium, fieldValues);
		requestDefault.setAdditionalFields(additionalFiled);

		PeopleFinderResponse peopleFinderResponse = typeAhead(requestDefault);
		List<Person> persons = peopleFinderResponse.getPersons();
		assertTrue("The response should receive one person only. query: "
				+ query, persons.size() == 1);
		assertTrue("The jobResponsibility was not received ", persons.get(0)
				.getJobResponsibility() != null);
		// assertTrue("The givenNames was not received " +
		// persons.get(0).getCountry()!= null) ;
	}

	@Test
	/**
	 * 112020
	 * Search for populated tag and check that the found person has all of the default fields, and tags match should include the specific tag
	 */
	public void testAdditionaFieldCheckConfidence()
			throws UnsupportedEncodingException {
		LOGGER.fine("Test PeopleFinderAdditionalFieldsTest#testAdditionaFieldCheckConfidence");
		String query = ProfileCreator.getRealName('B').split(" ")[1];
		PeopleFinderRequest requestDefault = new PeopleFinderRequest(query);
		// create additional field
		PeopleFinderAdditionalField additionalFiled = new PeopleFinderAdditionalField();

		Set<AdditionalFieldValues> fieldLowValues = new HashSet<AdditionalFieldValues>();
		fieldLowValues.add(AdditionalFieldValues.givenNames);

		additionalFiled
				.addFields(AdditionalFieldConfidence.low, fieldLowValues);

		Set<AdditionalFieldValues> fieldMediumValues = new HashSet<AdditionalFieldValues>();
		fieldMediumValues.add(AdditionalFieldValues.tag);
		additionalFiled.addFields(AdditionalFieldConfidence.medium,
				fieldMediumValues);

		Set<AdditionalFieldValues> fieldHighValues = new HashSet<AdditionalFieldValues>();
		fieldHighValues.add(AdditionalFieldValues.workPhone);
		additionalFiled.addFields(AdditionalFieldConfidence.high,
				fieldHighValues);

		requestDefault.setAdditionalFields(additionalFiled);

		PeopleFinderResponse peopleFinderResponse = typeAhead(requestDefault);
		assertTrue("The response should receive three people only. query: "
				+ query, peopleFinderResponse.getTotalResults() == 3);
		List<Person> persons = peopleFinderResponse.getPersons();

		List<Person> lowConfidencePeople = new ArrayList<Person>();
		List<Person> mediumConfidencePeople = new ArrayList<Person>();
		List<Person> highConfidencePeople = new ArrayList<Person>();

		for (Person person : persons) {
			String currentPersonConfidence = person.getConfidence();
			if (currentPersonConfidence.equals("low")) {
				lowConfidencePeople.add(person);
			} else if (currentPersonConfidence.equals("medium")) {
				mediumConfidencePeople.add(person);
			} else if (currentPersonConfidence.equals("high")) {
				highConfidencePeople.add(person);
			} else {
				fail("Confidance: " + currentPersonConfidence
						+ " is not in expected");
			}
		}

		handleLowConfidencePeople(lowConfidencePeople, additionalFiled);
		handleMediumAndHighConfidencePeople(mediumConfidencePeople,
				additionalFiled, AdditionalFieldConfidence.medium);
		handleMediumAndHighConfidencePeople(highConfidencePeople,
				additionalFiled, AdditionalFieldConfidence.high);
	}

	private void handleLowConfidencePeople(List<Person> lowConfidencePeople,
			PeopleFinderAdditionalField additionalFields) {
		if (lowConfidencePeople.size() > 0) {
			JSONArray lowFields = additionalFields
					.getFieldsByConfidence(AdditionalFieldConfidence.low);

			for (Person person : lowConfidencePeople) {
				checkFieldsThatMustBeReturned(person, lowFields,
						AdditionalFieldConfidence.low);
				isGivenNamesAvalable = (person.getGivenNames() != null) ? true
						: false;
				isPoneNumberAvalable = (person.getWorkPhone() != null) ? true
						: false;
				isAddressAvalable = (person.getCountry() != null) ? true
						: false;
				isTagsAvailable = (person.getTag() != null) ? true : false;
			}
		}

	}

	private void checkFieldsThatMustBeReturned(Person person,
			JSONArray lowFields, AdditionalFieldConfidence confidance) {
		for (Object additionalFieldObj : lowFields) {
			String additionalField = (String) additionalFieldObj;
			if (AdditionalFieldValues.givenNames.toString().equals(
					additionalField)) {
				assertTrue("The givenName should be returned in the "
						+ confidance.toString() + " entry ",
						person.getGivenNames() != null);
			} else if (AdditionalFieldValues.workPhone.toString().equals(
					additionalField)) {
				assertTrue(
						"The phone should be returned in the "
								+ confidance.toString() + " entry ",
						person.getWorkPhone() != null);
			} else if (AdditionalFieldValues.tag.toString().equals(
					additionalField)) {
				assertTrue(
						"The tag should be returned in the "
								+ confidance.toString() + " entry ",
						person.getTag() != null);
			}
		}
	}

	private void handleMediumAndHighConfidencePeople(
			List<Person> mediumConfidancePeople,
			PeopleFinderAdditionalField additionalFields,
			AdditionalFieldConfidence confidance) {
		if (mediumConfidancePeople.size() > 0) {
			JSONArray lowFields = additionalFields
					.getFieldsByConfidence(AdditionalFieldConfidence.low);
			for (Person person : mediumConfidancePeople) {
				checkFieldsThatMustBeReturned(person, lowFields,
						AdditionalFieldConfidence.low);

				if (isGivenNamesAvalable) {
					assertTrue(
							"The person with medium confidance is not include givenNames received by entrie with low confidance",
							person.getGivenNames() != null);
				} else {
					isGivenNamesAvalable = (person.getGivenNames() != null) ? true
							: false;
				}

				if (isPoneNumberAvalable) {
					assertTrue(
							"The person with medium confidance is not include phoneNumber received by entrie with low confidance",
							person.getWorkPhone() != null);
				} else {
					isGivenNamesAvalable = (person.getWorkPhone() != null) ? true
							: false;
				}

				if (isAddressAvalable) {
					assertTrue(
							"The person with medium confidance is not include address received by entrie with low confidance",
							person.getCountry() != null);
				} else {
					isGivenNamesAvalable = (person.getCountry() != null) ? true
							: false;
				}

				if (isTagsAvailable) {
					assertTrue(
							"The person with medium confidance is not include tag received by entrie with low confidance",
							person.getTag() != null);
				} else {
					isGivenNamesAvalable = (person.getGivenNames() != null) ? true
							: false;
				}
			}
		}
	}

	private void checkTags(Person person) {
		// tags
		String[] tags = person.getTag();
		assertTrue("The person " + person.getEmail()
				+ " does not include tags at all ", tags.length > 0);
		ArrayList<String> expectedTags = PopulatedData.getInstance()
				.getPeopleFinderTags();
		int containedTags = 0;
		for (String tag : tags) {
			if (tag.contains(expectedTags.get(0))
					|| tag.contains(expectedTags.get(1))) {
				containedTags++;
			}
		}
		assertTrue("The received tags should contain  " + expectedTags.get(0)
				+ " and " + expectedTags.get(1), containedTags == 2);

	}

	private PeopleFinderResponse typeAhead(PeopleFinderRequest requestDefault)
			throws UnsupportedEncodingException {
		ClientResponse responsePageSizeDefault = peopleFinderService
				.typeAhead(requestDefault);
		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(
				responsePageSizeDefault);
		return peopleFinderResponse;
	}

	private void checkPossibleFields(String query, Person person) {
		// address
		String address = person.getCountry();
		if (address != null) {
			assertTrue(address.contains(query));
		}
		org.apache.wink.json4j.JSONArray givenNames = person.getGivenNames();
		// given name
		if (givenNames != null) {
			Object[] givenNamesArray = givenNames.toArray();
			for (Object givenName : givenNamesArray) {
				String givenNameStr = (String) givenName;
				assertTrue("The given name " + givenNameStr
						+ "does not contain query:  " + query,
						givenNameStr.contains(query));
			}
		}
		// phone number
		String phoneNumber = person.getWorkPhone();
		if (phoneNumber != null) {
			assertTrue(phoneNumber.contains(query));
		}
	}

	private void checkDefaultFields(Person person,
			boolean verifyJobResponsibilityField) {
		assertNotNull("The id field should be returned by default",
				person.getId());
		assertNotNull("The email field should be returned by default",
				person.getEmail());
		assertNotNull("The name field should be returned by default",
				person.getName());
		assertNotNull("The userType field should be returned by default",
				person.getUserType());
		if (verifyJobResponsibilityField) {
			assertNotNull(
					"The jobResponsibility field should be returned by default",
					person.getJobResponsibility());
		}
		assertNotNull("The score field should be returned by default",
				person.getScore());
	}

}
