package com.ibm.conn.auto.webui.constants;

public final class OrientMeUIConstants {

    public static String switchHomepage = "css=a#headerHomepageLink";
    public static String staticMenuSection = "css=ul.static-head-sets li[role='button']";
    public static String staticMenuItems = staticMenuSection + " span[role='menuitem']";
    public static String responseIcon = staticMenuItems + "[aria-label='Responses to my content']";
    public static String mentionsIcon = staticMenuItems + "[aria-label='View Updates']";
    public static String allUpdatesIcon = staticMenuItems + "[aria-label='All Updates']";
    public static String filterBar = "xpath=//div[@aria-label='Filter Bar']";
    public static String responseFilter = filterBar+"//div[contains(text(), 'Responses')]";
    public static String filterRemoveIcon = "/../div[@title='Remove']";
    public static String topUpdates = "css=li#om_latest_updates";
    public static String latestUpdate = "css=li#om_latest_updates";
    public static String genericTypeahead = " ul.ui-typeahead-ul li.ui-typeahead-li";
    public static String shareSomethingTypeahead = "css=div#ui-editor-ta " + genericTypeahead;
    public static String importantToMeTypeahead = "css=div.itmtypeahead " + genericTypeahead;
    public static String filterTypeahead = "css=div.ic-web-filterbar-ta " + genericTypeahead;
    public static String hashtagText = "css=div > a[title^='Click here to search for the tag ##hashtag##']";
    public static String statusMentions1 = "css=div > a[title^='Click here to search for the tag ##hashtag##']+span[class='vcard']";
    public static String statusMentions2 = "css=div > a[title^='Click here to search for the tag ##hashtag##']+span[class='vcard']+span[class='vcard']";
    public static String filterLink = "css=button.ic-filter-tab";
    public static String filterBox = "css=div.ic-web-filterbar-text[role='textbox']";
    public static String allQuietImage = ".ic-home-svg";
    public static String tilesLoading = ".ic-tiles-animation-enter";
    
    // Important to Me
    // importantToMeListAll may include the Add Entry icon if present while importantToMeList omits it
    public static String importantToMeListAll = "css=ul.active-sets li.ic-bizcard";
    public static String importantToMeList = importantToMeListAll + ":not(.add-action)";
    public static String addEntryIcon = "css=ul.active-sets li.add-action";
    public static String suggestedImportantList = "css=ul.suggested-sets li.ic-bizcard:not(.add-action)";
    public static String addImportantItem = "css=section.ic-itm-bar button.add-contacts";
    public static String removeImportantItem = "css=section.ic-itm-bar button.remove-contacts";
    public static String addImportantBox = "css=input.typeaheadInput[type='text']";
    public static String importantToMeTypeaheadMore = "css=li.more-results";
    public static String bizCardIconInPersonIcon = "css=div.ic-bizcard-actions button[aria-label='Business card']";
    public static String viewCommunityInCommunityIcon = "css=button[aria-label='View Community']";
    public static String importantToMeIconLabel = "css=span.setLabel";
    
    // Share Something widget
    public static String shareSomething = "css=div.ui-editor";
    public static String shareSomethingBox = shareSomething + " div.public-DraftEditor-content";
    public static String shareSomethingPost = "css=article.ic-sharebox div.post-btn";
    public static String addFile ="css=article.ic-sharebox div.add-file";
    public static String addFileInput ="css=input[name='filename']";
    public static String postText ="xpath=//div[text()='##innertext##']";
    public static String likePost="xpath=//div[contains(text(),'##innertext##')]/parent::div/following-sibling::div/div[2]/div[2]/button";
    public static String moreActions="xpath=//div[contains(text(),'##innertext##')]/parent::div/following-sibling::div/div[2]/div[3]/button";
    public static String errorMessageText="css=.ic-messagetext";
    public static String repost="css=div.ic-actions-dlg-active [aria-label='Repost this Update']";
    public static String deletePost="css=div.ic-actions-dlg-active [aria-label='Delete']";
    public static String confirmDelete="css=button.bx--btn--primary";
    public static String confirmCancel="css=button.bx--btn--secondary";
    public static String postLink="xpath=//div[contains(text(),'##innertext##')]/a";
    public static String communityUpdate="//a[contains(text(),'##innertext##') and not(@class='ic-followed-entity-name')]/parent::span";
    public static String goToTop="css=.om-go-to-top-btn";
    public static String showMoreResults="css=.more-results";
    public static String imagePost="//div[contains(text(),'##innertext##')]/parent::div/following-sibling::div[1]/div[@class='ic-file-preview']/div[@class='ic-thumbnail']";
    public static String postList="xpath=//article[not(@class='ic-ac-tile-article') and not(@class='ic-sharebox ic-tile')]";
    public static String iframe="css=iframe";
    
    //Comments
    public static String buttonComment="xpath=//div[contains(text(),'##innertext##')]/parent::div/following-sibling::div/div[2]/div[1]/button";
    public static String commentEditor="xpath=//div[text()='Add a comment']/parent::div/following-sibling::div/div";
    public static String postComment="css=.ic-comment-form-post-btn.btn-active";
    public static String closeCommentSection="xpath=//button[@title='Close comment section']/parent::div";
    public static String commentCount=closeCommentSection + "/span";
    public static String commentText="xpath=//div[contains(text(),'##innertext##')]";
    
    // alert message
    public static String alertMessage = "css=div.notification-alert-container span#om_alert_message";
    
    // mentions
    public static String mentionsNotificationIcon = "css=li[aria-label^='Mentions'] > div [aria-label='View Updates']";
    public static String removeFilter="css=div[title='Remove']";
    public static String filterIconInPerson = "div.ic-bizcard-actions button[aria-label='Filter']";

    
    
    private OrientMeUIConstants() {}

}
