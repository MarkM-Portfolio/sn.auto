package com.ibm.conn.auto.webui;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.webui.cloud.FeedsUICloud;
import com.ibm.conn.auto.webui.onprem.FeedsUIOnPrem;

public class FeedsUI extends ICBaseUI{

	public FeedsUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	private static Logger log = LoggerFactory.getLogger(FeedsUI.class);
	
	public static String feedsSaveButton = "css=form[name='bookmarkAddForm'] input[value='Save']";
	public static String AddFeedFormTitle = "css=input[id='addFeedName']";
	public static String AddFeedFormFeed = "css=textarea[id='addFeedUrl']";
	public static String AddFeedFormDescription = "css=textarea[id='addFeedDescription']";
	public static String AddFeedFormTag = "css=input[id='autocompletetags2']";
	public static String EditFeedFormTag = "css=input[id^='autocompletetags_edit']";
	public static String EditFeedFormFeed = "css=textarea[id^='editFeedUrl']";
	public static String feedsEditSaveButton = "css=form[id^='editForm_'] input[value='Save']";
	public static String EditFeedFormDescription = "css=textarea[id^='editFeedDescription']";
	public static String EditFeedFormTitle = "css=input[id^='editFeedName']";
	public static String EditLink = "css=tbody tr td a:contains('Edit')";
	public static String feedsMoreLink = "css=a[id^='b_show_']:contains(More)";
	public static String AddFeedLink = "css=*[id='addFeedBtn']";
	public static String FeedDate = "css=span.date";
	public static String FeedTags= "css=span.lotusTags";
	public static String FeedElements = "css=tr[id^='b_summary_'] h4 a";
	public static String DeleteLink = "css=tbody tr td a:contains('Delete')";
	
	/**
	 * feedSpecificMore - to get specific feed's more link
	 * @param postion
	 * @return
	 */
	private String feedSpecificMore(String postion) {
		return "css=a[id='b_show_" + postion + "']:contains(More)";
	}
	
	/**
	 * feedSpecificEdit - to get specific feed's edit link
	 * @param postion
	 * @return
	 */
	private String feedSpecificEdit(String postion) {
		return "css=a[id='b_edit_" + postion + "']:contains(Edit)";
	}
	
	/**
	 * feedSpecificDelete - to get specific feed's delete link
	 * @param postion
	 * @return
	 */
	private String feedSpecificDelete(String postion) {
		return "css=tr[id='b_details_" + postion + "'] td a:contains('Delete')";
	}
	
	public void selectMoreLinkByFeed(BaseFeed feed) {
			
			String filePostion = null;
	
			log.info("INFO: Locate the More link associated with our file");
			List<Element> feeds = driver.getVisibleElements(FeedElements);
	
			if(feeds.size()>1){
				for(Element fileElement: feeds){
					if(fileElement.getText().contains(feed.getTitle())){
						log.info("INFO: Select More link");
						filePostion = fileElement.getAttribute("id").replace("b_uri_", "");
						clickLinkWait(feedSpecificMore(filePostion));
					}
				}
			}else{
				log.info("INFO: Select More link");	
				clickLinkWait(FilesUIConstants.moreLink);
				filePostion = null;
			}
		}
	
	public void selectEditLinkByFeed(BaseFeed feed) {
		
		String filePostion = null;

		log.info("INFO: Locate the Edit link associated with our file");
		List<Element> feeds = driver.getVisibleElements(FeedElements);

		if(feeds.size()>1){
			for(Element fileElement: feeds){
				if(fileElement.getText().contains(feed.getTitle())){
					log.info("INFO: Select Edit link");
					filePostion = fileElement.getAttribute("id").replace("b_uri_", "");
					driver.executeScript("arguments[0].scrollIntoView(true);",
                            driver.getElements(feedSpecificEdit(filePostion)).get(0).getWebElement());
					clickLinkWait(feedSpecificEdit(filePostion));
				}
			}
		}else{
			log.info("INFO: Select Edit link");	
			clickLinkWait(EditLink);
			filePostion = null;
		}
	}
	
	public void selectDeleteLinkByFeed(BaseFeed feed) {
			
			String filePostion = null;
	
			log.info("INFO: Locate the Delete link associated with our file");
			List<Element> feeds = driver.getVisibleElements(FeedElements);
	
			if(feeds.size()>1){
				for(Element fileElement: feeds){
					if(fileElement.getText().contains(feed.getTitle())){
						log.info("INFO: Select Delete link");
						filePostion = fileElement.getAttribute("id").replace("b_uri_", "");
						clickLinkWait(feedSpecificDelete(filePostion));
					}
				}
			}else{
				log.info("INFO: Select Delete link");	
				clickLinkWait(DeleteLink);
				filePostion = null;
			}
	}
	
	public void addFeed(BaseFeed feed) throws Exception{

		fluentWaitPresent(AddFeedFormFeed);
		
		//Add Feed URL
		log.info("INFO: Add Feed URL");
		driver.getSingleElement(AddFeedFormFeed).typeWithDelay(feed.getFeed());
		
		//Add Feed title
		log.info("INFO: Add Feed title");
		driver.getSingleElement(AddFeedFormTitle).type(feed.getTitle());

		//Add Feed Description
		if(!feed.getDescription().isEmpty()){
			log.info("INFO: Add Feed Description");
			driver.getSingleElement(AddFeedFormDescription).type(feed.getDescription());
		}

		//Add Feed Tags
		if(!feed.getDescription().isEmpty()){
			log.info("INFO: Add Feed Tags");
			driver.getSingleElement(AddFeedFormTag).typeWithDelay(feed.getTags());
		}

		//Save the Feed
		log.info("INFO: Save Feed");
		clickLink(BaseUIConstants.SaveButton);
		
		
	}
	
	public void editFeed(BaseFeed feed) throws Exception{
		
		//Edit Feed URL
		log.info("INFO: Edit Feed URL");
		Element feedurl = driver.getFirstElement(FeedsUI.EditFeedFormFeed);
		feedurl.clear();
		feedurl.type(feed.getFeed());
		
		//Edit Feed title
		log.info("INFO: Edit Feed title");
		Element title = driver.getFirstElement(FeedsUI.EditFeedFormTitle);
		title.clear();
		title.type(feed.getTitle());

		//Edit Feed Description
		if(!feed.getDescription().isEmpty()){
			log.info("INFO: Edit Feed Description");
			Element desc = driver.getFirstElement(FeedsUI.EditFeedFormDescription);
			desc.clear();
			desc.type(feed.getDescription());
		}

		//Edit Feed Tags
		if(!feed.getTags().isEmpty()){
			log.info("INFO: Edit Feed Tags");
			Element tags = driver.getFirstElement(FeedsUI.EditFeedFormTag);
			tags.clear();
			tags.typeWithDelay(feed.getTags());
		}

		//Save the Feed
		log.info("INFO: Save Feed");
		clickLink(feedsEditSaveButton);

	}
	
	public static FeedsUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  FeedsUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  FeedsUIOnPrem(driver);
		} else if(product.toLowerCase().equals("production")) {
			return new  FeedsUICloud(driver);
		} else if(product.toLowerCase().equals("vmodel")) {
			return new  FeedsUIOnPrem(driver);
		} else if(product.toLowerCase().equals("multi")) {
			return new  FeedsUIOnPrem(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
	
}
