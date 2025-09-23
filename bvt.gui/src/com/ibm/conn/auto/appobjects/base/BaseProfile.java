package com.ibm.conn.auto.appobjects.base;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.webui.ProfilesUI;

public class BaseProfile implements BaseStateObject{
	
	private static Logger log = LoggerFactory.getLogger(BaseProfile.class);
	
	public enum profileInfo {
		NAME,
		ADDRESS,
		FIRSTOTHERINFO,
		SECONDOTHERINFO,
		THIRDOTHERINFO,
		OFFNUM,
		MOBNUM,
		FAXNUM, 
		FIRSTOTHER, 
		SECONDOTHER, 
		THIRDOTHER,
		JOBTITLE,
		ABOUTME,
		BACKGROUND,
		PHOTO,
		BUILDING,
		FLOOR,
		OFFICE,
		IPTELEPHONE,
		PAGER,
		ALTERNATEEMAIL,
		BLOG,
		ASSISTANT,
		TINYEDITORFUNCTIONALITYTORUN;
	}
	
	public enum otherDropDown{
		OTHER("Other:",""),
		HOME("Home:",""),
		WORK("Work:",""),
		CELL("Cell:",""),
		FAX("Fax:","");
		
		public String action;
	    public String text;
	    
	    private otherDropDown(String action, String text){
	            this.action = action;
	            this.text = text;
	    }
	    
	    public String getMenuItem(){
			return this.action;
		}
	    
		public String getMenuItemText(){
			return this.text;
		}
		
		public otherDropDown setMenuItemText(String text){
			this.text = text;
			return this;
		}
	}
	
	private String name;
	private String address;
	private String firstOtherInfo;
	private String secondOtherInfo;
    private String thirdOtherInfo;
    private String officeNumber;
    private String mobileNumber;
    private String faxNumber;
    private otherDropDown firstOther;
    private otherDropDown secondOther;
    private otherDropDown thirdOther;
    private String jobTitle;
    private String background;
    private String aboutMe;
    private BaseFile photo;
    private String tinyEditorFunctionalityToRun;
    private Set<profileInfo> edit_track = new HashSet<profileInfo>();
    private String building;
    private String floor;
    private String office;
    private String ipTelephone;
    private String pager;
    private String alternateEmail;
    private String blog;
    private String assistant;
    
    public static class Builder {
    	private String name;
    	private String address;
    	private String firstOtherInfo;
    	private String secondOtherInfo;
        private String thirdOtherInfo;
        private String officeNumber;
        private String mobileNumber;
        private String faxNumber;
        private otherDropDown firstOther = otherDropDown.OTHER; //By default other is selected
        private otherDropDown secondOther = otherDropDown.OTHER; //By default other is selected
        private otherDropDown thirdOther = otherDropDown.OTHER; //By default other is selected
        private String jobTitle;
        private String tinyEditorFunctionalityToRun;
        private String background;
        private String aboutMe;
		private BaseFile photo = new BaseFile.Builder("").build();
		private String building;
	    private String floor;
	    private String office;
	    private String ipTelephone;
	    private String pager;
	    private String alternateEmail;
	    private String blog;
	    private String assistant;
		
		public Builder(String name){
			this.name = name;
		}
	
		public Builder address(String address){
			this.address = address;
			return this;
		}
	
		public Builder address1(String address1) {
			this.firstOtherInfo = address1;
			return this;
		}
		
		public Builder address2(String address2) {
			this.secondOtherInfo = address2;
			return this;
		}

		public Builder address3(String address3) {
			this.thirdOtherInfo = address3;
			return this;
		}
		
		public Builder jobTitle(String jobTitle) {
			this.jobTitle = jobTitle;
			return this;
		}

		public Builder tinyEditorFunctionalityToRun(String tinyEditorFunctionalityToRun) {
			this.tinyEditorFunctionalityToRun = tinyEditorFunctionalityToRun;
			return this;
		}
		
		public Builder officeNumber(String officeNumber) {
			this.officeNumber = officeNumber;
			return this;
		}
		
		public Builder mobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
			return this;
		}
		
		public Builder faxNumber(String faxNumber) {
			this.faxNumber = faxNumber;
			return this;
		}
		
		public Builder background(String background) {
			this.background = background;
			return this;
		}
		
		public Builder aboutMe(String aboutMe){
			this.aboutMe = aboutMe;
			return this;
		}
		
		public Builder other1(otherDropDown other1){
			this.firstOther = other1;
			return this;
		}
		
		public Builder other2(otherDropDown other2){
			this.secondOther = other2;
			return this;
		}
		
		public Builder other3(otherDropDown other3){
			this.thirdOther = other3;
			return this;
		}

		public Builder photo(BaseFile photo) {
			this.photo = photo;
			return this;
		}
		
		public Builder building(String building) {
			this.building = building;
			return this;
		}
		
		public Builder floor(String floor) {
			this.floor = floor;
			return this;
		}
		
		public Builder office(String office) {
			this.office = office;
			return this;
		}
		
		public Builder ipTelephone(String ipTelephone) {
			this.ipTelephone = ipTelephone;
			return this;
		}
		
		public Builder pager(String pager) {
			this.pager = pager;
			return this;
		}
		
		public Builder alternateEmail(String alternateEmail) {
			this.alternateEmail = alternateEmail;
			return this;
		}
		
		public Builder blog(String blog) {
			this.blog = blog;
			return this;
		}
		
		public Builder assistant(String assistant) {
			this.assistant = assistant;
			return this;
		}
		
		public BaseProfile build() {
			return new BaseProfile(this);
		}

	}
    
    private BaseProfile(Builder b) {
		this.setName(b.name);
		this.setAddress(b.address);
		this.setFirstOtherInfo(b.firstOtherInfo);
		this.setSecondOtherInfo(b.secondOtherInfo);
		this.setThirdOtherInfo(b.thirdOtherInfo);
		this.setJobTitle(b.jobTitle);
		this.setTinyEditorFunctionalityToRun(b.tinyEditorFunctionalityToRun);
		this.setOfficeNum(b.officeNumber);
		this.setMobileNum(b.mobileNumber);
		this.setFaxNum(b.faxNumber);
		this.setAboutMe(b.aboutMe);
		this.setBackground(b.background);
		this.setPhoto(b.photo);
		this.setFirstOther(b.firstOther);
		this.setSecondOther(b.secondOther);
		this.setThirdOther(b.thirdOther);
		this.setBuilding(b.building);
		this.setFloor(b.floor);
		this.setOffice(b.office);
		this.setIPTelephone(b.ipTelephone);
		this.setPager(b.pager);
		this.setAlternateEmail(b.alternateEmail);
		this.setBlog(b.blog);
		this.setAssistant(b.assistant);
		this.edit_track.clear();
	}
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTinyEditorFunctionalityToRun() {
		return tinyEditorFunctionalityToRun;
	}

	public void setTinyEditorFunctionalityToRun(String tinyEditorFunctionalityToRun) {
		this.tinyEditorFunctionalityToRun = tinyEditorFunctionalityToRun;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.edit_track.add(profileInfo.ADDRESS);
		this.address = address;
	}
    
	public String getFirstOtherInfo() {
		return firstOtherInfo;
	}

	public void setFirstOtherInfo(String address1) {
		this.edit_track.add(profileInfo.FIRSTOTHERINFO);
		this.firstOtherInfo = address1;
	}
	
	public String getSecondOtherInfo() {
		return secondOtherInfo;
	}

	public void setSecondOtherInfo(String address2) {
		this.edit_track.add(profileInfo.SECONDOTHERINFO);
		this.secondOtherInfo = address2;
	}
	
	public String getThirdOtherInfo() {
		return thirdOtherInfo;
	}

	public void setThirdOtherInfo(String address3) {
		this.edit_track.add(profileInfo.THIRDOTHERINFO);
		this.thirdOtherInfo = address3;
	}
	
	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.edit_track.add(profileInfo.JOBTITLE);
		this.jobTitle = jobTitle;
	}
	
	public String getOfficeNum() {
		return officeNumber;
	}

	public void setOfficeNum(String officeNumber) {
		this.edit_track.add(profileInfo.OFFNUM);
		this.officeNumber = officeNumber;
	}
	
	public String getMobileNum() {
		return mobileNumber;
	}

	public void setMobileNum(String mobileNumber) {
		this.edit_track.add(profileInfo.MOBNUM);
		this.mobileNumber = mobileNumber;
	}
	
	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.edit_track.add(profileInfo.ABOUTME);
		this.aboutMe = aboutMe;
	}
	
	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.edit_track.add(profileInfo.BACKGROUND);
		this.background = background;
	}
	
	public String getFaxNum() {
		return faxNumber;
	}

	public void setFaxNum(String faxNumber) {
		this.edit_track.add(profileInfo.FAXNUM);
		this.faxNumber = faxNumber;
	}
	
	public BaseFile getPhoto(){
		return photo;
	}
	
	public void setPhoto(BaseFile photo){
		this.edit_track.add(profileInfo.PHOTO);
		this.photo = photo;
	}
	
	public otherDropDown getFirstOther(){
		return firstOther;
	}
	
	public void setFirstOther(otherDropDown other1){
		this.edit_track.add(profileInfo.FIRSTOTHER);
		this.firstOther = other1;
	}
	
	public otherDropDown getSecondOther(){
		return secondOther;
	}
	
	public void setSecondOther(otherDropDown other2){
		this.edit_track.add(profileInfo.SECONDOTHER);
		this.secondOther = other2;
	}
	
	public otherDropDown getThirdOther(){
		return thirdOther;
	}
	
	public void setThirdOther(otherDropDown other3){
		this.edit_track.add(profileInfo.THIRDOTHER);
		this.thirdOther = other3;
	}
	
	public String getBuilding() {
		return building;
	}
	
	public void setBuilding(String building){
		this.edit_track.add(profileInfo.BUILDING);
		this.building = building;
	}
	
	public String getFloor() {
		return floor;
	}
	
	public void setFloor(String floor){
		this.edit_track.add(profileInfo.FLOOR);
		this.floor = floor;
	}
	
	public String getOffice() {
		return office;
	}
	
	public void setOffice(String office){
		this.edit_track.add(profileInfo.OFFICE);
		this.office = office;
	}
	
	public String getIPTelephone() {
		return ipTelephone;
	}
	
	public void setIPTelephone(String ipTelephone){
		this.edit_track.add(profileInfo.IPTELEPHONE);
		this.ipTelephone = ipTelephone;
	}
	
	public String getPager() {
		return pager;
	}
	
	public void setPager(String pager){
		this.edit_track.add(profileInfo.PAGER);
		this.pager = pager;
	}
	
	public String getAlternateEmail() {
		return alternateEmail;
	}
	
	public void setAlternateEmail(String alternateEmail){
		this.edit_track.add(profileInfo.ALTERNATEEMAIL);
		this.alternateEmail = alternateEmail;
	}
	
	public String getBlog() {
		return blog;
	}
	
	public void setBlog(String blog){
		this.edit_track.add(profileInfo.BLOG);
		this.blog = blog;
	}
	
	public String getAssistant() {
		return assistant;
	}
	
	public void setAssistant(String assistant){
		this.edit_track.add(profileInfo.ASSISTANT);
		this.assistant = assistant;
	}
	
	public void edit(ProfilesUI ui){
		log.info("INFO: Edit Profile");
		ui.editProfile(this);
		this.edit_track.clear();
	}
	
	public Set<profileInfo> getEdits() {
		return edit_track;
	}
	
	public String verifyTinyEditor(ProfilesUI ui)  {
		String TEText = ui.verifyTinyEditorInProfile(this);
		return TEText;
	}
	
	
}
