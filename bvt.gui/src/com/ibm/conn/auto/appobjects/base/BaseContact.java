package com.ibm.conn.auto.appobjects.base;

import java.util.LinkedHashSet;
import java.util.Set;

import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.webui.ProfilesUI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseContact implements BaseStateObject{

	private static Logger log = LoggerFactory.getLogger(BaseContact.class);
	
	public enum contactFields {
		TITLE,
		GIVEN,
		MIDDLE,
		SURNAME,
		SUFFIX,
		JOBTITLE,
		ORG, 
		ADDRESS, 
		INFORMATION, 
		NOTES, 
		PRIMEMAIL, 
		PRIMETELE, 
		RELATION,
		NAME_ORDER,
		PHOTO;
	}
	
	public enum contactNameOrder{
		FIRST_NAME_FIRST,
		LAST_NAME_FIRST;
	}
	
	private String title;
	private String given;
	private String middle;
	private String surname;
	private String suffix;
	private String jobTitle;
	private String org;
	private String primEmail;
	private String primTele;
	private String relation;
	private String address;
	private String information;
	private String notes;
	private contactNameOrder nameOrder;
	private Set<contactFields> edit_track = new LinkedHashSet<contactFields>();
	private BaseFile photo;
	
	public static class Builder {
		private String title;
		private String given;
		private String middle;
		private String surname;
		private String suffix;
		private String jobTitle;
		private String org;
		private String primEmail;
		private String primTele;
		private String relation;
		private String address;
		private String information;
		private String notes;
		private contactNameOrder nameOrder = contactNameOrder.FIRST_NAME_FIRST; //default to this format
		private BaseFile photo = new BaseFile.Builder("").build();
		
		public Builder(String given, String surname){
			this.given = given;
			this.surname = surname;
		}
	
		public Builder title(String title) {
			this.title = title;
			return this;
		}
	
		public Builder middle(String middle) {
			this.middle = middle;
			return this;
		}
		
		public Builder suffix(String suffix) {
			this.suffix = suffix;
			return this;
		}

		public Builder jobTitle(String jobTitle) {
			this.jobTitle = jobTitle;
			return this;
		}
		
		public Builder org(String org) {
			this.org = org;
			return this;
		}
		
		public Builder primEmail(String primEmail) {
			this.primEmail = primEmail;
			return this;
		}
		
		public Builder primTele(String primTele) {
			this.primTele = primTele;
			return this;
		}
		
		public Builder relation(String relation) {
			this.relation = relation;
			return this;
		}
		
		public Builder address(String address){
			this.address = address;
			return this;
		}
		
		public Builder information(String information){
			this.information = information;
			return this;
		}
		
		public Builder notes(String notes){
			this.notes = notes;
			return this;
		}
		
		public Builder nameOrder(contactNameOrder nameOrder){
			this.nameOrder = nameOrder;
			return this;
		}

		public Builder photo(BaseFile photo) {
			this.photo = photo;
			return this;
		}
		
		public BaseContact build() {
			return new BaseContact(this);
		}
	}
	
	private BaseContact(Builder b) {
		this.setTitle(b.title);
		this.setGiven(b.given);
		this.setMiddle(b.middle);
		this.setSurname(b.surname);
		this.setSuffix(b.suffix);
		this.setJobTitle(b.jobTitle);
		this.setOrg(b.org);
		this.setPrimEmail(b.primEmail);
		this.setPrimTele(b.primTele);
		this.setRelation(b.relation);
		this.setAddress(b.address);
		this.setNotes(b.notes);
		this.setInformation(b.information);
		this.setNameOrder(b.nameOrder);
		this.setPhoto(b.photo);
		this.edit_track.clear();
	}
	
	/**
	 * This method returns the title of a person such as:<br>
	 * Dr, Mr, Mrs, Ms. <br>
	 * This may be identified as "prefix" in some parts of Connections
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.edit_track.add(contactFields.TITLE);
		this.title = title;
	}
	
	public String getGiven() {
		return given;
	}

	public void setGiven(String given) {
		this.edit_track.add(contactFields.GIVEN);
		this.given = given;
	}	
	
	public String getMiddle() {
		return middle;
	}

	public void setMiddle(String middle) {
		this.edit_track.add(contactFields.MIDDLE);
		this.middle = middle;
	}
	
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.edit_track.add(contactFields.SURNAME);
		this.surname = surname;
	}
	
	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.edit_track.add(contactFields.SUFFIX);
		this.suffix = suffix;
	}
	
	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.edit_track.add(contactFields.JOBTITLE);
		this.jobTitle = jobTitle;
	}
	
	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.edit_track.add(contactFields.ORG);
		this.org = org;
	}
	
	public String getPrimEmail() {
		return primEmail;
	}

	public void setPrimEmail(String primEmail) {
		this.edit_track.add(contactFields.PRIMEMAIL);
		this.primEmail = primEmail;
	}
	
	public String getPrimTele() {
		return primTele;
	}

	public void setPrimTele(String primTele) {
		this.edit_track.add(contactFields.PRIMETELE);
		this.primTele = primTele;
	}
	
	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.edit_track.add(contactFields.RELATION);
		this.relation = relation;
	}
	
	public String getAddress(){
		return address;
	}
	
	public void setAddress(String address){
		this.edit_track.add(contactFields.ADDRESS);
		this.address=address;
	}
	
	public String getNotes(){
		return notes;
	}
	
	public void setNotes(String notes){
		this.edit_track.add(contactFields.NOTES);
		this.notes = notes;
	}
	
	public String getInformation(){
		return information;
	}
	
	public void setInformation(String information){
		this.edit_track.add(contactFields.INFORMATION);
		this.information = information;
	}

	public contactNameOrder getNameOrder(){
		return nameOrder;
	}
	
	public void setNameOrder(contactNameOrder nameOrder){
		this.edit_track.add(contactFields.NAME_ORDER);
		this.nameOrder = nameOrder;
	}
	
	public BaseFile getPhoto(){
		return photo;
	}
	
	public void setPhoto(BaseFile photo){
		this.edit_track.add(contactFields.PHOTO);
		this.photo = photo;
	}
	
	public void create(ProfilesUI ui) {
		ui.createContact(this);
		this.edit_track.clear();
	}
	
	public void createForEditAndDelete(ProfilesUI ui){
		ui.createContactWithoutNav(this);
		this.edit_track.clear();
	}
	
	public void delete(ProfilesUI ui){
		ui.deleteSocialContact(this);
	}
	
	public void edit(ProfilesUI ui){
		ui.editContact(this);
		this.edit_track.clear();
	}
	
	public Set<contactFields> getEdits() {
		return edit_track;
	}
	
	/**
	 * This method returns one of two formats:
	 * <ul>
	 * <li>Title</li>
	 * <li>Surname</li>
	 * <li>Given</li>
	 * <li>Middle</li>
	 * <li>Suffix</li>
	 * </ul>
	 * which would look like: <b>Title Lastname Firstname Middlename Suffix </b><br>
	 * Ex: <b>Dr. Smith John James Jr.</b><br><br>
	 * 
	 * <ul>
	 * <li>Title (prefix for a name such as Mr./Mrs./Ms./Dr.)</li>
	 * <li>Given (The first name of the person)</li>
	 * <li>Middle (The middle name of a person</li>
	 * <li>Surname (A person's last name)</li>
	 * <li>Suffix (endings to last names)</li>
	 * </ul>
	 * Which would look like: <b>Title Firstname Middlename Lastname Suffix </b><br>
	 * Ex: <b>Dr. John James Smith Jr.</b>
	 * @author Matthew Maffa
	 */
	public String getFullname(){
		/*
		 * Create the string that we will return back to the user. We don't want this existing outside 
		 * the method in the form of a private or public variable since the user needs to call this method
		 * to get an updated version of the full name, in case information has changed. Additionally, 
		 * keeping a class variable would waste resources since every time that the full name would be called
		 * 
		 * Checks are done on all fields to ensure that we are not typing in empty strings nor the word "null"
		 */
		log.info("Calculating the full name that appears on a contacts edit or create page");
		String fullname="";
		if (this.getTitle() != "" &&  this.getTitle() != null)
			fullname += this.getTitle() + " ";
		if (nameOrder == contactNameOrder.LAST_NAME_FIRST)
			if (this.getSurname() != "" && this.getSurname() != null)
				fullname += this.getSurname() + " ";
		if (this.getGiven() != "" &&  this.getGiven() != null)
			fullname += this.getGiven() + " ";
		if (this.getMiddle() != "" && this.getMiddle() !=null)
			fullname += this.getMiddle() + " ";
		if (nameOrder == contactNameOrder.FIRST_NAME_FIRST)
			if (this.getSurname() != "" && this.getSurname() != null)
				fullname += this.getSurname() + " ";
		if (this.getSuffix() != "" && this.getSuffix() != null)
			fullname += this.getSuffix() + " ";
		fullname = fullname.trim();
		return fullname;
	}
	
	/**
	 * <b>This will prevent exceptions being thrown for null strings</b><br>
	 * <i>This is designed to minimize the code length and allow easier maintenance of the code</i>
	 * @param toCheck - The String that you are checking if it has text in it
	 * @return <b>true</b> if the String passed is not null and is not an empty String <br>
	 * <b>false</b> if the passed String is null or empty
	 */
	public String getAppearName(){
		log.info("INFO: Calculating what the name will appear as on the contact selection page.");
		String appearName="";
		if (nameOrder == contactNameOrder.LAST_NAME_FIRST){
			if (Helper.containsText(this.getSurname()))
				appearName = this.getSurname() + " ";
			if (Helper.containsText(this.getGiven()))
				appearName += this.getGiven() + " ";
			if (Helper.containsText(this.getMiddle()))
				appearName += this.getMiddle();
		}else{
			if(Helper.containsText(this.getGiven()))
				appearName = this.getGiven() + " ";
			if(Helper.containsText(this.getMiddle()))
				appearName += this.getMiddle() + " ";
			if(Helper.containsText(this.getSurname()))
				appearName += this.getSurname();
		}
		log.info("INFO: Name is:"+appearName);
		return appearName.trim();
	}
}
