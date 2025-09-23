package com.ibm.conn.auto.webui.constants;

public final class ForumsUIConstants {


	/** Start a forum */
	public static String Forum_Tab = "xpath=//a[@tab='FORUMS'] | //ul[@class='lotusTabs']/li/a[text()='Forums']";
	public static String Start_A_Forum = "xpath=//a[text()='Start a Forum']";
	public static String Start_A_Forum_InputText_Name = "css=input#lconn_forums_ForumForm_0_name.lotusText";
	public static String Start_A_Forum_InputText_Tags = "css=input#lconn_forums_ForumForm_0_tag";
	public static String Start_A_Forum_Textarea_Description = "css=textarea#lconn_forums_ForumForm_0_description";
	public static String Start_A_Forum_New_Topic_Notification_Enabled = "css=input[id='lconn_forums_ForumForm_0_autoFollowMark'][checked='true']";
	public static String Start_A_Forum_New_Topic_Notification_Disabled = "css=input[id='lconn_forums_ForumForm_0_autoFollowMark'][checked='false']";
	public static String Forum_Auto_Follow_Option = "css=input[name='autoFollow']";
	public static String Save_Forum_Button = "css=input[id='addForumButton']";
	/** selectors */
	public static String Centre_Content_Filter_Tabs_Tab1 = "css=div.lotusContent>div>div>div>div.lotusTabContainer>div>ul>li:nth(0) a";
	public static String My_Forums_Tab = "link=My Forums";
	public static String Im_An_Owner = "link=I'm an Owner";
	public static String Im_Following = "link=I'm Following";
	public static String Public_Forums_Tab = "link=Public Forums";
	public static String Reply_to_topic = "css=div[class='lotusBtnContainer'] span[class='lotusBtn lotusBtnAction'] a:contains(Reply to Topic)";
	public static String ReplyTopic = "css=span.forumReplyAction a:contains(Reply)";
	public static String Join_Comm_To_Reply = "css=div.lotusActions span.forumMemberShipAction a:contains(Join this community to reply)";
	public static String First_Topic_Number_of_Replies = "css=td[class='lotusMeta'] span[class='lotusRight']";
	public static String OpenQuestionsTab = "css=li#dfOpenQuestionsTab > a,a[tab='QUESTIONS']";
	public static String AnsweredQuestionsTab = "css=li#dfAnsweredQuestionsTab > a";
	public static String OpenQuestionsTab_SA = "css=ul.lotusTabs li a:contains(Open Questions)";
	public static String AnsweredQuestionsTab_SA = "css=ul.lotusTabs li a:contains(Answered Questions)";
	public static String communityForumsTab = "css=li[id='dfForumsTab'] a:contains(Forums)";
	public static String forumNameLink = "css=div[class='lotusBreadcrumbs'] span[class='bidiAware']";
	//sort by links
	public static String RepliesLink = "link=Replies";
	public static String LikesLink = "link=Likes";
	/** topic actions */
	public static String Start_A_Topic = "link=Start a Topic";
	public static String Start_A_Topic_Community = "css=tr#startTopicLink a, div[id='dfTopicList'] span[class='lotusBtn lotusBtnAction']>a,div[id='dfTopicList'] span[class*='lotusBtn lotusBtnAction']>a";
	public static String Start_A_Topic_InputText_Title = "css=input#lconn_forums_PostForm_0_postTitle";
	public static String Start_A_Topic_InputCheckbox_MarkAsQuestion = "css=input#lconn_forums_PostForm_0_postQestionMark";
	public static String Start_A_Topic_InputText_Tags = "css=input#lconn_forums_PostForm_0_postTag";
	public static String Start_A_Topic_InputText_Description ="css=textarea#lconn_forums_PostForm_0_description";
	public static String Save_Forum_Topic_Button = "css=input[id='addTopicButton']";
	public static String AddRemove_Tags = "link=Add or Remove Tags";
	public static String AddTag = "css=form input.lotusText[title='Input tags:']";
	public static String AddTag_OkButton = "css=span#lconn_forums_TagEditor_0.lotusTags form input.lotusBtn";
	public static String EditTopic = "css=ul.lotusInlinelist li a:contains(Edit):nth(0)";
	public static String EditReplyTopic = "css=ul.lotusInlinelist.forumPostActionToolBarIndicator li a:contains(Edit):nth(1)";
	public static String MarkTopicAsQuestion = "css=label:contains(Mark this topic as a question)";
	public static String UnansweredQuestion = "css=img[alt='Unanswered question']";
	public static String AnsweredQuestion = "css=img[alt='Answered question']";
	public static String DeclineAnswer = "link=Decline this Answer";
	public static String ReopenQuestion = "link=Reopen Question";
	public static String AcceptAsAnswer = "link=Accept this Answer";
	public static String PinTopicLink = "link=Pin this Topic";
	public static String UnPinTopicLink = "link=Unpin this Topic";
	public static String AddTags = "css=a[class='tagAdd lotusAction']";
	public static String topComponentForumLink = "css=span[class='lotusText'] a:contains(Forums)";
	public static String EditFirstReply = "css=ul.lotusInlinelist li a:contains(Edit):nth(0)";
	public static String Reply_Title ="id=lconn_forums_PostForm_0_postTitle";
	public static String Edit_Reply_Title  = "css=div.lotusFormField a.lotusAction[dojoattachevent='onclick:editTitle' ]";
	/** reply to ckeditor parameters*/
	public static String forumReplyToCkEditor_frame = "css=iframe[class='cke_wysiwyg_frame cke_reset']";
	public static String forumReplyToCkEditor_body = "css=body[contenteditable='true']";
	/** Mega Menu options */
	public static String forumsOption = "css=a>strong:contains(Forums)";
	public static String forumsImAnOwner = "css=tr:contains(Forums)>td>a:contains(I'm an Owner)";
	public static String forumsPublicForums = "css=td>a:contains(Public Forums)";
	/** Following */
	public static String Forum_Follow_Actions = "css=#forumFollowMenu";//Following Actions button
	public static String Follow_Forum = "css=#dijit_MenuItem_0_text";
	public static String Forum_Following_Message = "You are following this forum and will receive updates about it.";
	public static String Save_Topic_Reply = "css=input[value='Save']";
	public static String deleteButton = "css=input[value='Delete']";
	public static String Forum_ActionMember = "css=a[id='forumActionsMenu']";
	public static String Forum_ActionMember_Opt = "css=td[id^='dijit_MenuItem_']";
	public static String Stop_Following_Topic = "css=tbody[class='dijitReset'] tr:contains(Stop Following this Topic)";
	public static String Stop_Following_Forum = "css=tbody[class='dijitReset'] tr:contains(Stop Following this Forum)";
	public static String Stop_Following_Community = "css=tbody[class='dijitReset'] tr:contains(Stop Following this Community)";
	public static String Start_Following_Forum = "css=tbody[class='dijitReset'] tr:contains(Follow this Forum)";
	public static String Start_Following_Topic = "css=tbody[class='dijitReset'] tr:contains(Follow this Topic)";
	public static String Start_Following_Community = "css=tbody[class='dijitReset'] tr:contains(Follow this Community)";
	/* Members and owners */
	public static String ForumsAddOwnersInput = "css=div.AddMembers > div.field > input.lotusText";
	public static String RemoveOwner = "css=img[alt='Remove this member']";
	public static String AddOwners = "link=Add Owners";
	public static String MemberOkButton = "css=div.lotusDialog div.lotusDialogFooter input.submit";
	public static String MemberTable = "css=div[id^=lconn_forums_AddMembers_][id$=_input_dropdown]";
	public static String MemberNames = "css=div.dijitMenuItem[id^=lconn_forums_AddMembers_][role='option']";
	public static String MemberSearchDir = "css=div[id^=lconn_forums_AddMembers_][id$=_input_popup_searchDir]";
	/** UI */
	public static String TopicList = "css=tr.normalTopic";
	public static String PinedTopicList = "css=tr.pinnedTopic";
	public static String LikeLink = "css=a[id^=TOGGLE_lconn_forums_like_Inline_]";
	public static String forumDeleteMsg = "css=div[class='lotusMessageBody']:contains(The forum has been successfully deleted.)";
	public static String QuestionIcon = "css=div.lconnSprite.lconnSprite-iconQuestion16";
	public static String topicDeleteMsg = "css=div[class='lotusMessageBody']:contains(The topic has been successfully deleted.)";
	/*UI items for pin/unpin a topic*/
	public static String PinnedSuccessfulMsg = "css=div.lotusMessageBody:contains(This topic has been successfully pinned.)";
	public static String UnpinnedSuccessfulMsg = "css=div.lotusMessageBody:contains(This topic has been successfully unpinned.)";
	public static String TopicTr = "css=tr[class*='Topic']";
	public static String HighlightedCssClass = "lotusPinnedRow";
	public static String PinnedTopicCssClass = "pinnedTopic";
	public static String PagingBar =  "css=ul.pageList";
	public static String Page2Link = "css=ul.pageList a[title='Page 2 of 2']";
	public static String MessageDivOnTopicsTab = "css=div#dfTopicList div.lotusChunk";
	/**
	 * Related Communities
	 */
	public static String RelatedCommunityURL = "id=lconn_recomm_AddForm_0_URL";
	public static String RelatedCommunityName = "id=lconn_recomm_AddForm_0_Name";
	public static String RelatedCommunityDesc = "id=lconn_recomm_AddForm_0_Description";
	public static String AddACommunityLink = "css=div#recomm a:contains('Add a Community')";
	public static String ViewAllLink = "css=div#recomm a:contains('View All')";
	public static String MoreLink = "link=More";
	public static String EditLink = "link=Edit";
	public static String RemoveThisCommunityLink = "link=Remove this community";
	public static String bizCardLink = "css=div.vcard.lotusLikeAvatarLink a.lotusPerson";
	public static String bizCard = "css=div#semtagmenuCard";
	/**
	 * ui items for like/unlike a topic/reply
	 */
	public static String LikeReplyLink = "css=a#TOGGLE_lconn_forums_like_Inline_1.lotusLikeAction";
	public static String UnlikeLink = "link=Unlike";
	public static String likeDiv = "css=div.lotusLike";
	public static String likeReplyDiv = "css=div.lotusLike:nth(1)";
	public static String likeNumber = "css=a.lotusLikeCount div.lotusLikeText";
	public static String likeReplyNumber = "css=a.lotusLikeCount div.lotusLikeText:nth(1)";
	public static String likeNumberOnList = "css=div.lotusLike a.lconnLikeCountNoBackground div.lotusLikeText";
	public static String likeNumberOverview = "css=div#list.forumPagedList div.lotusLike";
	public static String likeImg = "css=div#list.forumPagedList div.lotusLike img.lotusIconLike";
	public static String OnePersonLikesThis = "css=div.lotusLike a.lotusLikeCount";
	public static String OnePersonLikesThisReply = "css=div.lotusLike a.lotusLikeCount:nth(1)";
	public static String OnePersonLikesThisOnList = "css=div.lotusLike a.lconnLikeCountNoBackground.lotusDisabled";
	public static String NoPeopleLiksThisOnList = "css=div.lotusLike a.lconnLikeCountNoBackground";
	public static String likeWidget = "css=div.lotusPopupContent div.lotusLikeLightBox";
	public static String likeHeader = "css=div.lotusLikeHeader span.lotusLikeHeaderText";
	/**
	 * attachment
	 */
	public static String Attach_A_File = "link=Attach a File";
	public static String RemoveAttachment = "link=Remove";
	public static String ReplaceAttachment = "link=Replace";
	public static String AttachInput = "css=input[name=attachments]";
	public static String AttachOKBtn = "css=input.lotusFormButton[value=OK]";
	public static String AttachHeader = "css=div.lotusPostContent:contains(Attachments)";
	public static String AttachThumbnail = "css=ul.dfAttachment  img.dfThumbnail";
	public static String AttachPreviewLink = "css=ul.dfAttachment a.dfLink";
	public static String AttachDownloadLink = "css=ul.dfAttachment a.dfDownload";
	public static String AttachSize = "css=ul.dfAttachment li.dfSize";
	public static String AttachFirstLi = "css=ul.dfAttachments li:nth(0)";
	public static String TopicUpdateMeta = "css=div.topicUpdateMeta";
	public static String ForumDesc = "css=div.entry-content.lotusPostDetails";
	public static String forumReplyDesc="css=ul.lotusCommentList div.entry-content.lotusPostDetails";
	public static String forumTopicImage = "css=div.lotusPostContent div p img";
	
	//Language Selector
	public static String topicsImfollowingHeadline = "//span[@id='myForumTableDescription']";
	
	//CNX8UI Element
	public static String StartATopic = "//a[contains(text(),'Start a Topic')]";
	public static String allForumsLink = "//a[contains(text(),'All Forums')]";
	public static String dateSortDesc = "//a[@id='resultContainer_View_Sorting_date_Bttn' and @title='Sort by [Date]']";
	public static String feedLink = "//a[contains(text(),'Feed for these Search Results')]";
	
	/**
	 * Edit Forum Topic
	 */
	public static String editForumTopic = "xpath=//a[contains(text(),'Edit')]";

	/**
	 * Share Forum Topic
	 */
	public static String shareForumTopic = "a[class='MuiTypography-root MuiLink-root MuiLink-underlineHover MuiTypography-colorPrimary']";
	public static String likeImage = "//img[@class='lotusIconLike']";
	public static String deleteLink = "//a[text()='Delete Forum']";
	public static String likedUserpopupCloseIcon = "//a[@dojoattachpoint='closeNode']";
	public static String replyToTopicBtnnewUI = "xpath=//span[contains(text(),'PLACEHOLDER')]//ancestor::div[@class='lotusPagedContent']//li[contains(@class,'forumNotQestionIndicator')]//span[@class='forumReplyAction']//a";
	public static String publicForumsLinksCNX8UI = "//a[@aria-label='Public Forums']";

	private ForumsUIConstants() {}

}
