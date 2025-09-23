package com.ibm.lconn.automation.framework.services.profiles.nodes;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * VCardEntry is an Entry that contains a VCard within it.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class VCardEntry extends LCEntry {
	
	private HashMap<String, Element> editableFields;
	private LinkedHashMap<String, String> vCardFields;

	private final static Logger LOGGER = Logger.getLogger(VCardEntry.class.getName());
	
	public VCardEntry(String content, HashMap<String, Element> editableFields) {
		
		vCardFields = new LinkedHashMap<String, String>();
		setEditableFields(editableFields);
		
		for(String line : content.split("\n")) {
			String[] lineContent = line.split(":", 2);
			if(lineContent.length == 2) {
				vCardFields.put(lineContent[0], lineContent[1]);
			}
		}
	}

	public HashMap<String, Element> getEditableFields() {
		return editableFields;
	}

	public void setEditableFields(HashMap<String, Element> editableFields) {
		this.editableFields = editableFields;
	}
	
	public void setVCardField(String field, String vcardField, String value) {
		if(editableFields.containsKey(field)) {
			vCardFields.put(vcardField, value);
		} else {
			LOGGER.warning(field + " is not editable. Please use wsadmin to modify this field.");
		}
	}
	
	public void setAlternateLastname(String alternateLastname) {
		setVCardField(StringConstants.ALTERNATE_LASTNAME, StringConstants.VCARD_ALTERNATE_LASTNAME, alternateLastname);
	}
	
	public void setBldgId(String bldgId) {
		setVCardField(StringConstants.BLDGID, StringConstants.VCARD_BLDGID, bldgId);
	}
	
	public void setBlogUrl(String blogUrl) {
		setVCardField(StringConstants.BLOG_URL, StringConstants.VCARD_BLOG_URL, blogUrl);
	}
	
	public void setCountryCode(String countryCode) {
		setVCardField(StringConstants.COUNTRY_CODE, StringConstants.VCARD_COUNTRY_CODE, countryCode);
	}
	
	public void setCourtesyTitle(String courtesyTitle) {
		setVCardField(StringConstants.COURTESY_TITLE, StringConstants.VCARD_COURTESY_TITLE, courtesyTitle);
	}
	
	public void setDeptNumber(String deptNumber) {
		setVCardField(StringConstants.DEPT_NUMBER, StringConstants.VCARD_DEPT_NUMBER, deptNumber);
	}
	
	public void setDescription(String description) {
		setVCardField(StringConstants.DESCRIPTION, StringConstants.VCARD_DESCRIPTION, description);
	}
	
	public void setDisplayName(String displayName) {
		setVCardField(StringConstants.DISPLAY_NAME, StringConstants.VCARD_DISPLAY_NAME, displayName);
	}
	
	public void setEmail(String email) {
		setVCardField(StringConstants.EMAIL, StringConstants.VCARD_EMAIL, email);
	}
	
	public void setEmployeeNumber(String employeeNumber) {
		setVCardField(StringConstants.EMPLOYEE_NUMBER, StringConstants.VCARD_EMPLOYEE_NUMBER, employeeNumber);
	}
	
	public void setEmployeeTypeCode(String employeeTypeCode) {
		setVCardField(StringConstants.EMPLOYEE_TYPE_CODE, StringConstants.VCARD_EMPLOYEE_TYPE_CODE, employeeTypeCode);
	}
	
	public void setEmployeeTypeDesc(String employeeTypeDesc) {
		setVCardField(StringConstants.EMPLOYEE_TYPE_DESC, StringConstants.VCARD_EMPLOYEE_TYPE_DESC, employeeTypeDesc);
	}
	
	public void setExperience(String experience) {
		setVCardField(StringConstants.EXPERIENCE, StringConstants.VCARD_EXPERIENCE, experience);
	}
	
	public void setFaxNumber(String faxNumber) {
		setVCardField(StringConstants.FAX_NUMBER, StringConstants.VCARD_FAX_NUMBER, faxNumber);
	}
	
	public void setFloor(String floor) {
		setVCardField(StringConstants.FLOOR, StringConstants.VCARD_FLOOR, floor);
	}
	
	public void setGroupwareEmail(String groupwareEmail) {
		setVCardField(StringConstants.GROUPWARE_EMAIL, StringConstants.VCARD_GROUPWARE_EMAIL, groupwareEmail);
	}
	
	public void setGuid(String guid) {
		setVCardField(StringConstants.GUID, StringConstants.VCARD_GUID, guid);
	}
	
	public void setIPTelephoneNumber(String ipTelephoneNumber) {
		setVCardField(StringConstants.IP_TELEPHONE_NUMBER, StringConstants.VCARD_IP_TELEPHONE_NUMBER, ipTelephoneNumber);
	}
	
	public void setIsManager(String isManager) {
		setVCardField(StringConstants.IS_MANAGER, StringConstants.VCARD_IS_MANAGER, isManager);
	}
	
	public void setJobResp(String jobResp) {
		setVCardField(StringConstants.JOB_RESP, StringConstants.VCARD_JOB_RESP, jobResp);
	}
	
	public void setLastUpdate(String lastUpdate) {
		setVCardField(StringConstants.LAST_UPDATE, StringConstants.VCARD_JOB_RESP, lastUpdate);
	}
	
	public void setManagerUid(String managerUid) {
		setVCardField(StringConstants.MANAGER_UID, StringConstants.MANAGER_UID, managerUid);
	}
	
	public void setMobileNumber(String mobileNumber) {
		setVCardField(StringConstants.MOBILE_NUMBER, StringConstants.VCARD_MOBILE_NUMBER, mobileNumber);
	}
	
	public void setNativeFirstName(String nativeFirstName) {
		setVCardField(StringConstants.NATIVE_FIRST_NAME, StringConstants.VCARD_NATIVE_FIRST_NAME, nativeFirstName);
	}
	
	public void setNativeLastName(String nativeLastName) {
		setVCardField(StringConstants.NATIVE_LAST_NAME, StringConstants.VCARD_NATIVE_LAST_NAME, nativeLastName);
	}
	
	public void setOfficeName(String officeName) {
		setVCardField(StringConstants.OFFICE_NAME, StringConstants.VCARD_OFFICE_NAME, officeName);
	}
	
	public void setOrganizationTitle(String organizationTitle) {
		setVCardField(StringConstants.ORGANIZATION_TITLE, StringConstants.VCARD_ORGANIZATION_TITLE, organizationTitle);
	}
	
	public void setOrgId(String orgId) {
		setVCardField(StringConstants.ORG_ID, StringConstants.VCARD_ORG_ID, orgId);
	}
	
	public void setPagerId(String pagerId) {
		setVCardField(StringConstants.PAGER_ID, StringConstants.VCARD_PAGER_ID, pagerId);
	}
	
	public void setPagerServiceProvider(String pagerServiceProvider) {
		setVCardField(StringConstants.PAGER_SERVICE_PROVIDER, StringConstants.VCARD_PAGER_SERVICE_PROVIDER, pagerServiceProvider);
	}
	
	public void setPagerType(String pagerType) {
		setVCardField(StringConstants.PAGER_TYPE, StringConstants.VCARD_PAGER_TYPE, pagerType);
	}
	
	public void setPreferredFirstName(String preferredFirstName) {
		setVCardField(StringConstants.PREFERRED_FIRST_NAME, StringConstants.VCARD_PREFERRED_FIRST_NAME, preferredFirstName);
	}
	
	public void setPreferredLanguage(String preferredLanguage) {
		setVCardField(StringConstants.PREFERRED_LANGUAGE, StringConstants.VCARD_PREFERRED_LANGUAGE, preferredLanguage);
	}
	
	public void setPreferredLastName(String preferredLastName) {
		setVCardField(StringConstants.PREFERRED_LAST_NAME, StringConstants.VCARD_PREFERRED_LAST_NAME, preferredLastName);
	}
	
	public void setTelephoneNumber(String telephoneNumber) {
		setVCardField(StringConstants.TELEPHONE_NUMBER, StringConstants.VCARD_TELEPHONE_NUMBER, telephoneNumber);
	}
	
	public void setTimezone(String timezone) {
		setVCardField(StringConstants.TIMEZONE, StringConstants.VCARD_TIMEZONE, timezone);
	}
	
	public void setUid(String uid) {
		setVCardField(StringConstants.UID, StringConstants.VCARD_UID, uid);
	}
	
	public void setUrl(String url) {
		setVCardField(StringConstants.URL, StringConstants.VCARD_URL, url);
	}
	
	public void setWorkLocation(String workLocation) {
		setVCardField(StringConstants.WORK_LOCATION, StringConstants.VCARD_WORK_LOCATION, workLocation);
	}
	
	public void setWorkLocationCode(String workLocationCode) {
		setVCardField(StringConstants.WORK_LOCATION_CODE, StringConstants.VCARD_WORK_LOCATION_CODE, workLocationCode);
	}
	
	public LinkedHashMap<String, String> getVCardFields() {
		return vCardFields;
	}

	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		entry.setTitle(vCardFields.get(StringConstants.VCARD_UID));
		entry.setContent(this.toString());
		
		Category isProfileCategory = getFactory().newCategory();
		isProfileCategory.setScheme(StringConstants.SCHEME_TYPE);
		isProfileCategory.setTerm("profile");
		entry.addCategory(isProfileCategory);
		
		return entry;
	}
	
	@Override
	public String toString() {
		String vCardString = "";
		
		for(Map.Entry<String, String> entry : vCardFields.entrySet()) {
			vCardString += entry.getKey() + ":" + entry.getValue() + "\n";
		}
		
		return vCardString;
	}
	
}
