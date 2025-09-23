package com.ibm.lconn.automation.framework.services.search.response;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;

public class PeopleFinderResponse {
	protected final static Logger LOGGER = Logger.getLogger(PeopleFinderResponse.class.getName());
	private String peopleFinderResponseStr;
	private int numResultsInCurrentPage = 0;
	private int totalResults = 0;
	private List<Person> persons;
	private static final List<String> fieldsList = Arrays.asList("id", "name", "userType", "email", "score", "jobResponsibility","givenNames","workPhone","mobilePhone", "tieLine","country", "state", "city", "location", "postalCode","confidence", "tag");
	
	public PeopleFinderResponse(ClientResponse response) {
		int status = response.getStatus();
		if (status != 200) {
			fail("Fail to excecute - status : " + status);
		}
		try {
			peopleFinderResponseStr = readResponse(response.getReader());

		} catch (IOException e) {
			fail("Can not read the response: " + e.getLocalizedMessage());
		}
		LOGGER.fine(peopleFinderResponseStr);
		init();
	}

	public String readResponse(Reader responseReader) {
		if (responseReader == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		try {
			int charValue = 0;
			while ((charValue = responseReader.read()) != -1) {
				sb.append((char) charValue);
			}
		} catch (IOException e) {
		}
		return sb.toString();
	}

	public void init() {
		OrderedJSONObject jsonResponse;
		JSONArray jsonPersonsArr = null;
		try {
			jsonResponse = new OrderedJSONObject(peopleFinderResponseStr);
			numResultsInCurrentPage = jsonResponse.getInt("numResultsInCurrentPage");
			totalResults = jsonResponse.getInt("totalResults");
			jsonPersonsArr = jsonResponse.getJSONArray("persons");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		persons =  convertJsonPersonsArrToListOfPersons(jsonPersonsArr);
		StringBuilder sb = new StringBuilder();
		sb.append("numResultsInCurrentPage: ").append(numResultsInCurrentPage).append("\n");
		sb.append("totalResults: ").append(totalResults).append("\n");
		sb.append("persons details: ").append(jsonPersonsArr).append("\n");
		sb.append("persons: ").append(persons.size()).append("\n");
		LOGGER.fine(sb.toString());
	}

	private List<Person> convertJsonPersonsArrToListOfPersons(JSONArray jsonPersonsArr) {
		List<Person> persons = new ArrayList<Person>();
		for (Object object : jsonPersonsArr) {
			OrderedJSONObject jsonPerson = (OrderedJSONObject)object;
			Map <String, Object> personDetails = new HashMap<String, Object>();
				for (String field : fieldsList) {
					try {
					personDetails.put(field, jsonPerson.getString(field)); 
					} catch (JSONException e) {
						continue;
					}
				}
			
			persons.add(new Person(personDetails));
			
		}
		return persons;
	}
	public String getPeopleFinderResponseStr() {
		return peopleFinderResponseStr;
	}

	public int getNumResultsInCurrentPage() {
		return numResultsInCurrentPage;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public List<Person> getPersons() {
		return persons;
	}


	public class Person {
		private Map <String, Object> personDetails;

		Person(Map <String, Object> personDetails) {
			this.personDetails = personDetails;
			
		}

		@Override
		public int hashCode() {
			String id = getId();
			String name = getName();
			String userType = getUserType();
			String email= getEmail();
			
			
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((email == null) ? 0 : email.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((userType == null) ? 0 :userType.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			String id = getId();
			String name = getName();
			String userType = getUserType();
			String email= getEmail();
			
			Person other = (Person) obj;
			String otherId = other.getId();
			String otherName = other.getName();
			String otherUserType = other.getUserType();
			String otherEmail= other.getEmail();
			
//			if (!getOuterType().equals(other.getOuterType()))
//				return false;
			if (email == null) {
				if (otherEmail != null)
					return false;
			} else if (!email.equals(otherEmail))
				return false;
			if (id == null) {
				if (otherId != null)
					return false;
			} else if (!id.equals(otherId))
				return false;
			if (name == null) {
				if (otherName != null)
					return false;
			} else if (!name.equals(otherName))
				return false;
			if (userType == null) {
				if (otherUserType != null)
					return false;
			} else if (!userType.equals(otherUserType))
				return false;
			return true;
		}

		public String getId() {
			return (String)personDetails.get("id");
		}

		public String getName() {
			return  (String)personDetails.get("name");
		}

		public String getUserType() {
			return  (String)personDetails.get("userType");
		}

		public String getEmail() {
			return (String) personDetails.get("email");
		}
		
		public String getJobResponsibility() {
			return  (String)personDetails.get("jobResponsibility");
		}
		
		public JSONArray getGivenNames() {
			JSONArray givenNames = null;
			Object givenNamesObject = personDetails.get("givenNames");
			
			if (givenNamesObject == null) {
				return null;
			}
			
			try {
				givenNames =   new JSONArray((String)givenNamesObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return givenNames;
		}
		
		public String getWorkPhone() {
			return  (String)personDetails.get("workPhone");
		}
		
		public String getCountry() {
			return  (String)personDetails.get("country");
		}
		
		public String[] getTag() {
			String tagsStr  =  (String) personDetails.get("tag");
			return  convertToArray(tagsStr);
		}
				
		public float getScore() {
			String score =  (String)personDetails.get("score");
			return  Float.parseFloat(score);
		}
		public String getConfidence() {
			String confidence =  (String)personDetails.get("confidence");
			return  confidence;
		}

		private PeopleFinderResponse getOuterType() {
			return PeopleFinderResponse.this;
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("Person - ");
			for(Map.Entry<String, Object> entry: personDetails.entrySet()){
				sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
			}
			return sb.toString();
		}
	}


	/**
	 * 
	 * @param realName
	 * @param email
	 * @return person if exist, null if the person was not found
	 */
	public Person isPersonExist(String realName, String email) {
		Person p = null;
		for (Person person : persons) {
			LOGGER.fine("Compare: " + person.getName() + " - " + realName + "\n" + person.getEmail() + " - " + email);
			String personName = person.getName().replace("<B>", "");
			personName = personName.replace("</B>", "");
			String personEmail=null;
			if (person.getEmail()!= null){
			personEmail = person.getEmail().replace("<B>", "");
			personEmail = personEmail.replace("</B>", "");
			}
			if(personName.equals(realName.trim()) && ((personEmail==null)|| personEmail.equals(email.trim())  )) {
				p = person;
				break;
			}
		}
		return p;
	}
	
	private String[] convertToArray(String tags) {
		if (tags == null) {
			return null;
		}
		tags = tags.replace("[", "");
		tags = tags.replace("]", "");
		tags = tags.replace("\"", "");
		return tags.split(",");
		
	}
}
