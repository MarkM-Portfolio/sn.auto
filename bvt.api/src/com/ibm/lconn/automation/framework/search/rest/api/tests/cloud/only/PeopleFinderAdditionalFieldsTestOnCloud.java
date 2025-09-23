package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.MyProfileUpdate;
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
public class PeopleFinderAdditionalFieldsTestOnCloud {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance().getPeopleFinderLogger();

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
		peopleFinderService = new PeopleFinderService(searchUser.getAbderaClient(), searchServiceEntry);

	}

	@Test
	public void testCheckAdditionaFieldTag() throws Exception {
		LOGGER.fine("Test PeopleFinderAdditionalFieldsTest#testCheckAdditionaField");

		String query = URLEncoder.encode(MyProfileUpdate.realNameWithTag, "UTF-8");
		PeopleFinderRequest requestDefault = new PeopleFinderRequest(query);

		PeopleFinderAdditionalField additionalFiled = new PeopleFinderAdditionalField();
		Set<AdditionalFieldValues> fieldValues1 = new HashSet<AdditionalFieldValues>();
		fieldValues1.add(AdditionalFieldValues.tag);
		additionalFiled.addFields(AdditionalFieldConfidence.high, fieldValues1);
		requestDefault.setAdditionalFields(additionalFiled);

		PeopleFinderResponse peopleFinderResponse = typeAhead(requestDefault);

		List<Person> persons = peopleFinderResponse.getPersons();

		assertTrue("No people found. query: " + query, persons.size() > 0);

		for (Person person : persons) {
			checkPossibleFieldTag(person);
		}

	}

	private PeopleFinderResponse typeAhead(PeopleFinderRequest requestDefault) throws UnsupportedEncodingException {
		ClientResponse responsePageSizeDefault = peopleFinderService.typeAhead(requestDefault);
		PeopleFinderResponse peopleFinderResponse = new PeopleFinderResponse(responsePageSizeDefault);
		return peopleFinderResponse;
	}

	private void checkPossibleFieldTag(Person person) {

		assertNotNull("The tag field should be returned ", person.getTag());
	}

	private void checkDefaultFields(Person person, boolean verifyJobResponsibilityField) {
		assertNotNull("The id field should be returned by default", person.getId());
		assertNotNull("The email field should be returned by default", person.getEmail());

		assertNotNull("The name field should be returned by default", person.getName());
		assertNotNull("The userType field should be returned by default", person.getUserType());
		if (verifyJobResponsibilityField) {
			assertNotNull("The jobResponsibility field should be returned by default", person.getJobResponsibility());
		}
		assertNotNull("The score field should be returned by default", person.getScore());
	}

}
