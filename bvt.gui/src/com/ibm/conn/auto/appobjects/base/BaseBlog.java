package com.ibm.conn.auto.appobjects.base;

import java.util.HashSet;
import java.util.Set;
import org.testng.Assert;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.BlogsUI.EditVia;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class BaseBlog implements BaseStateObject{

	public enum blogFields {
		NAME,
		TAGS,
		DESCRIPTION,
		TIMEZONE,
		ISACTIVE,
		USE_EMOTICONS,
		ALLOWCOMMENTS,
		COMMENTSTIME,
		APPLYEXISTENTRY,
		EDITOTHERCOMMENTS,
		MODERATECOMMENTS,
		COMMEMBERROLE;	
	}
	
	private String name;
	private String blogAddress;
	private String tags;
	private String description;
	private Time_Zone timeZone;
	private Theme theme;
	private BaseCommunity community;
	private boolean isActive = true;
	private boolean allowComments = true;
	private comTime commentsTime = comTime._UNLIMITED;
	private boolean applyExistEntry = false;
	private boolean useEmoticons = false;
	private boolean moderateComments = false;
	private boolean editOtherComments = true;
	private Set<blogFields> edit_track = new HashSet<blogFields>();
	private MemberRole comMemberRole;
	private String tinyEditorFunctionalitytoRun;
	
	public static class Builder {
		private String name;
		private String blogAddress;
		private String tags = "";
		private String description;
		private Time_Zone timeZone = Time_Zone.America_Lima;
		private Theme theme = Theme.Blog;
		private BaseCommunity community = null;
		private String tinyEditorFunctionalitytoRun;
		
		public Builder(String name, String blogAddress){
			this.name = name;
			this.blogAddress = blogAddress;
		}

		public Builder tinyEditorFunctionalitytoRun(String functionality)
		{
			this.tinyEditorFunctionalitytoRun= functionality;
			return this;
		}
		
		public Builder tags(String tags){
			this.tags = tags;
			return this;
		}
	
		public Builder blogAddress(String blogAddress){
			this.blogAddress = blogAddress;
			return this;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Builder timeZone(Time_Zone timeZone) {
			this.timeZone = timeZone;
			return this;
		}
		
		public Builder theme(Theme theme) {
			this.theme = theme;
			return this;
		}
		
		public Builder community(BaseCommunity community){
			this.community = community;
			return this;
		}
		
		public BaseBlog build() {
			return new BaseBlog(this);
		}
			
	}
		
		private BaseBlog(Builder blogObj) {
			this.setName(blogObj.name);
			this.setBlogAddress(blogObj.blogAddress);			
			this.setTags(blogObj.tags);
			this.setDescription(blogObj.description);
			this.setTimeZone(blogObj.timeZone);
			this.setTheme(blogObj.theme);
			this.setCommunity(blogObj.community);
			this.setTinyEditorFunctionalitytoRun(blogObj.tinyEditorFunctionalitytoRun);
		}
		
		public void setTinyEditorFunctionalitytoRun(String functionality)
		{
			this.tinyEditorFunctionalitytoRun = functionality;
		}
		
		public String getTinyEditorFunctionalitytoRun() {
			return tinyEditorFunctionalitytoRun;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.edit_track.add(blogFields.NAME);
			this.name = name;
		}
		
		public String getBlogAddress() {
			return blogAddress;
		}

		public void setBlogAddress(String blogAddress) {
			this.blogAddress = blogAddress;
		}
		
		
		public String getTags() {
			return tags;
		}

		public void setTags(String tags) {
			this.edit_track.add(blogFields.TAGS);
			this.tags = tags;
		}
		
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.edit_track.add(blogFields.DESCRIPTION);
			this.description = description;
		}

		public Time_Zone getTimeZone() {
			return timeZone;
		}

		public void setTimeZone(Time_Zone timeZone) {
			this.edit_track.add(blogFields.TIMEZONE);
			this.timeZone = timeZone;
		}
		
		public Theme getTheme() {
			return theme;
		}

		public void setTheme(Theme theme) {
			this.theme = theme;
		}

		public BaseCommunity getCommunity() {
			return community;
		}

		public void setCommunity(BaseCommunity community) {
			this.community = community;
		}

		public boolean getIsActive() {
			return isActive;
		}
		
		public void setIsActive(boolean isActive) {
			this.edit_track.add(blogFields.ISACTIVE);
			this.isActive = isActive;
		}
		
		public boolean getUseEmoticons() {
			return useEmoticons;
		}
		
		public void setUseEmoticons(boolean useEmoticons) {
			this.edit_track.add(blogFields.USE_EMOTICONS);
			this.useEmoticons = useEmoticons;
		}
		
		public boolean getAllowComments() {
			return allowComments;
		}
		
		public void setAllowComments(boolean allowComments) {
			this.edit_track.add(blogFields.ALLOWCOMMENTS);
			this.allowComments = allowComments;
		}
		
		public boolean getModerateComments() {
			return moderateComments;
		}
		
		public void setModerateComments(boolean moderateComments) {
			this.edit_track.add(blogFields.MODERATECOMMENTS);
			this.moderateComments = moderateComments;
		}
		
		public comTime getCommentsTime() {
			return commentsTime;
		}
		
		public void setCommentsTime(comTime commentsTime) {
			this.edit_track.add(blogFields.COMMENTSTIME);
			this.commentsTime = commentsTime;
		}
		
		public boolean getApplyExistEntry() {
			return applyExistEntry;
		}
		
		public void setApplyExistEntry(boolean applyExistEntry) {
			this.edit_track.add(blogFields.APPLYEXISTENTRY);
			this.applyExistEntry = applyExistEntry;
		}
		
		public boolean getEditOtherComments() {
			return editOtherComments;
		}
		
		public void setEditOtherComments(boolean editOtherComments) {
			this.edit_track.add(blogFields.EDITOTHERCOMMENTS);
			this.editOtherComments = editOtherComments;
		}
		
		public MemberRole getComMemberRole() {
			return comMemberRole;
		}
		
		public void setComMemberRole(MemberRole comMemberRole){
			this.edit_track.add(blogFields.COMMEMBERROLE);
			this.comMemberRole = comMemberRole;
		}
		
		
		public enum comTime {
			
			_UNLIMITED("unlimited days"),
			_1DAY("1 day"),
			_2DAYS("2 days"),
			_3DAYS("3 days"),
			_4DAYS("4 days"),
			_5DAYS("5 days"),
			_7DAYS("7 days"),
			_10DAYS("10 days"),
			_20DAYS("20 days"),
			_30DAYS("30 days"),
			_60DAYS("60 days"),
			_90DAYS("90 days");
			
		    public String commentDays;
		    private comTime(String commentDays){
		            this.commentDays = commentDays;
		    }
		    
		    @Override
		    public String toString(){
		            return commentDays;
		    }
			
		}
		
		public enum Time_Zone {
			
			Etc_GMT_plus12("(GMT-12:00) International Date Line West"),
			Pacific_Pago_Pago("(GMT-11:00) Midway Island, Samoa"),
			Pacific_Honolulu("(GMT-10:00) Hawaii"),
			America_Anchorage("(GMT-09:00) Alaska"),
			America_Los_Angeles("(GMT-08:00) Pacific Time (US & Canada)"),
			America_Tijuana("(GMT-08:00) Tijuana, Baja California"),
			America_Phoenix("(GMT-07:00) Arizona"),
			America_Chihuahua("(GMT-07:00) Chihuahua, La Paz, Mazatlan"),
			America_Denver("(GMT-07:00) Mountain Time (US & Canada)"),
			America_Guatemala("(GMT-06:00) Central America"),
			America_Chicago("(GMT-06:00) Central Time (US & Canada)"),
			America_Mexico_City("(GMT-06:00) Guadalajara, Mexico City, Monterey"),
			America_Winnipeg("(GMT-06:00) Saskatchewan"),
			America_Lima("(GMT-05:00) Bogota, Lima, Quito"),
			America_New_York("(GMT-05:00) Eastern Time (US & Canada)"),
			America_Indianapolis("(GMT-05:00) Indiana (East)"),
			America_Halifax("(GMT-04:00) Atlantic Time (Canada)"),
			America_La_Paz("(GMT-04:00) Caracas, La Paz"),
			America_Manaus("(GMT-04:00) Manaus"),
			America_Santiago("(GMT-04:00) Santiago"),
			America_St_Johns("(GMT-03:30) Newfoundland"),
			America_Sao_Paulo("(GMT-03:00) Brasilia"),
			America_Buenos_Aires("(GMT-03:00) Buenos Aires, Georgetown"),
			America_Godthab("(GMT-03:00) Greenland"),
			America_Montevideo("(GMT-03:00) Montevideo"),
			Atlantic_South_Georgia("(GMT-02:00) Mid-Atlantic"),
			Atlantic_Azores("(GMT-01:00) Azores"),
			Atlantic_Cape_Verde("(GMT-01:00) Cape Verde Is."),
			Africa_Casablanca("(GMT) Casablanca, Monrovia"),
			Europe_London("(GMT) Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London"),
			Europe_Amsterdam("(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna"),
			Europe_Belgrade("(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague"),
			Europe_Brussels("(GMT+01:00) Brussels, Copenhagen, Madrid, Paris"),
			Europe_Warsaw("(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb"),
			Africa_Lagos("(GMT+01:00) West Central Africa"),
			Asia_Amman("(GMT+02:00) Amman"),
			Europe_Athens("(GMT+02:00) Athens, Bucharest, Istanbul"),
			Asia_Beirut("(GMT+02:00) Beirut"),
			Africa_Cairo("(GMT+02:00) Cairo"),
			Africa_Harare("(GMT+02:00) Harare, Pretoria"),
			Europe_Helsinki("(GMT+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius"),
			Asia_Jerusalem("(GMT+02:00) Jerusalem"),
			Europe_Minsk("(GMT+02:00) Minsk"),
			Africa_Windhoek("(GMT+02:00) Windhoek"),
			Asia_Baghdad("(GMT+03:00) Baghdad"),
			Asia_Kuwait("(GMT+03:00) Kuwait, Riyadh"),
			Europe_Moscow("(GMT+03:00) Moscow, St. Petersburg, Volgograd"),
			Africa_Nairobi("(GMT+03:00) Nairobi"),
			Asia_Tbilisi("(GMT+03:00) Tbilisi"),
			Asia_Tehran("(GMT+03:30) Tehran"),
			Asia_Muscat("(GMT+04:00) Abu Dhabi, Muscat"),
			Asia_Baku("(GMT+04:00) Baku"),
			Asia_Yerevan("(GMT+04:00) Yerevan"),
			Asia_Kabul("(GMT+04:30) Kabul"),
			Asia_Yekaterinburg("(GMT+05:00) Ekaterinburg"),
			Asia_Karachi("(GMT+05:00) Islamabad, Karachi, Tashkent"),
			Asia_Calcutta("(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi"),
			Asia_Colombo("(GMT+05:30) Sri Jayawardenepura"),
			Asia_Katmandu("(GMT+05:45) Kathmandu"),
			Asia_Almaty("(GMT+06:00) Almaty, Novosibirsk"),
			Asia_Dhaka("(GMT+06:00) Astana, Dhaka"),
			Asia_Rangoon("(GMT+06:30) Yangon (Rangoon)"),
			Asia_Bangkok("(GMT+07:00) Bangkok, Hanoi, Jakarta"),
			Asia_Krasnoyarsk("(GMT+07:00) Krasnoyars"),
			Asia_Hong_Kong("(GMT+08:00) Beijing, Chongqing, Hong Kong, Urumqi"),
			Asia_Irkutsk("(GMT+08:00) Irkutsk, Ulaan Bataar"),
			Asia_Singapore("(GMT+08:00) Kuala Lumpur, Singapore"),
			Australia_Perth("(GMT+08:00) Perth"),
			Asia_Taipei("(GMT+08:00) Taipei"),
			Asia_Tokyo("(GMT+09:00) Osaka, Sapporo, Tokyo"),
			Asia_Seoul("(GMT+09:00) Seoul"),
			Asia_Yakutsk("(GMT+09:00) Yakutsk"),
			Australia_Adelaide("(GMT+09:30) Adelaide"),
			Australia_Darwin("(GMT+09:30) Darwin"),
			Australia_Brisbane("(GMT+10:00) Brisbane"),
			Australia_Sydney("(GMT+10:00) Canberra, Melbourne, Sydney"),
			Pacific_Guam("(GMT+10:00) Guam, Port Moresby"),
			Australia_Hobart("(GMT+10:00) Hobart"),
			Asia_Vladivostok("(GMT+10:00) Vladivostok"),
			Pacific_Noumea("(GMT+11:00) Magadan, Solomon Is., New Caledonia"),
			Pacific_Auckland("(GMT+12:00) Auckland, Wellington"),
			Pacific_Fiji("(GMT+12:00) Fiji, Kamchatka, Marshall Is."),
			Pacific_Tongatapu("(GMT+13:00) Nuku'alofa");

			
		    public String name;
		    private Time_Zone(String brand){
		            this.name = brand;
		    }
		    
		    @Override
		    public String toString(){
		            return name;
		    }

		}		
		
		public enum Theme {
			Blog("Blog"),
			Blog_with_Bookmarks("Blog_with_Bookmarks"),
			Blog_with_Multiple_Authors("Blog_with_Multiple_Authors"),
			Khaki_Standard("Khaki_Standard"),
			Khaki_with_Bookmarks("Khaki_with_Bookmarks"),
			Khaki_with_Multiple_Authors("Khaki_with_Multiple_Authors"),
			Slate_Standard("Slate_Standard"),
			Slate_with_Bookmarks("Slate_with_Bookmarks"),
			Slate_with_Muliple_Authors("Slate_with_Muliple_Authors");
			
		    public String name;
		    private Theme(String brand){
		            this.name = brand;
		    }
		    
		    @Override
		    public String toString(){
		            return name;
		    }

		}
		
		public enum MemberRole{
			AUTHOR("authorRole"),
			DRAFT("draftRole"),
			VIEWER("viewerRole");
			
		    public String id;
		    private MemberRole(String id){
		            this.id = id;
		    }
		    
		    @Override
		    public String toString(){
		            return id;
		    }
		}
		
		public Set<blogFields> getEdits(){
			return edit_track;
		}
		
		public void create(BlogsUI blogsUI) {
			blogsUI.create(this);
			
			//after creating blog reset edits
			this.edit_track.clear();	
		}
		
		public void delete(BlogsUI blogsUI){
			blogsUI.delete(this);
		}
		
		public void edit(BlogsUI ui, EditVia editType){
			ui.edit(this, editType);
			
			//after creating blog reset edits
			this.edit_track.clear();	
		}
		
		/**
		 * Add a stand-alone Blog via API 
		 * @param APIBlogsHandler apiOwner
		 * @return Blog API object
		 * @see createAPI(APICommunitiesHandler apiOwner) if you want to add community level blog
		 */
		public Blog createAPI(APIBlogsHandler apiOwner){			
			Blog blog = apiOwner.createBlog(this);
			Assert.assertTrue(blog != null, "Failed to add blog using API.");	

			//after creating blog reset edits
			this.edit_track.clear();		
			return blog;			
		}
		
		/**
		 * Add a Community Blog via API
		 * @param APIBlogsHandler apiOwner
		 * @param Community A Community to add the Blog to
		 * @return Community Blog API object
		 * @see createAPI(APIBlogsHandler apiOwner) if you want to add application level blog
		 */
		public Blog createAPI(APIBlogsHandler apiOwner, Community community){			
			Blog blog = apiOwner.createBlog(this, community);
			Assert.assertTrue(blog != null, "Failed to add community blog using API.");		
			return blog;			
		}
		
		public void addMember(BlogsUI ui, Member member) {
			ui.addMember(member);
		}
		
		/**
		 * Performs actions in TinyEditor and verifies output 
		 * @param BlogsUI builder object
		 * @param User testUser1 to get username to mention
		 * @return text from tiny editor
		 */
		public String verifyTinyEditor(BlogsUI ui, User testUser1)  {
			String TEText = ui.verifyTinyEditorInBlog(this,testUser1);
			return TEText;
		}
}
