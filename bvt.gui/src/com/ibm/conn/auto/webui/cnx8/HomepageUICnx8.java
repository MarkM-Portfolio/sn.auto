package com.ibm.conn.auto.webui.cnx8;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;

public class HomepageUICnx8 extends HCBaseUI {

	protected static Logger log = LoggerFactory.getLogger(HomepageUICnx8.class);

	public HomepageUICnx8(RCLocationExecutor driver) {
		super(driver);
	}

	/**
	 * This method returns the image icon in specified news story
	 * @param statusUpdate
	 * @param file
	 * @return
	 */
	public static String getFileImageIconOfNewsStory(String statusUpdate, BaseFile file) {
		return "//ul[@id='asPermLinkAnchor']//div[contains(@aria-label,'" + statusUpdate
				+ "')]//div[@dojoattachpoint='imageCellNode']/child::img[@title='" + file.getRename()
				+ file.getExtension() + "']";
	}

	/**
     * Method to load component based on toggleUI switch value.
     * @param loginUser - user used for login
     */
	public void loadComponentAndLogin(User loginUser)
	{
		if(cfg.getUseNewUI())

		{
			log.info("Load Homepage, Log In as: " + loginUser.getDisplayName() + " and Toggle to New UI as "+cfg.getUseNewUI());
			loadComponent(Data.getData().ComponentHomepage);
			loginAndToggleUI(loginUser, true);

		}
		else
		{
			log.info("Load Homepage I am following, Log In as: " + loginUser.getDisplayName() + " and Toggle to New UI as "+cfg.getUseNewUI());
			loadComponent(Data.getData().HomepageImFollowing);
			loginAndToggleUI(loginUser, false);

		}
	}

	/**
     * Method to comment on Discover post.
     * @param commentMessage - message to post in comment.
     * @param postMessage - String used to generate post message locator.
     */
	public void commentOnDiscoverPost(String commentMessage,String postMessage) {

		switchToFrame(findElement(By.xpath(HomepageUIConstants.discoverComment_iFrame.replace("PLACEHOLDER1", postMessage))));
		typeWithDelayWd(commentMessage, By.xpath(HomepageUIConstants.discoverCommentBody));
		driver.switchToFrame().returnToTopFrame();	
	}
	
	/**
     * Method to comment on Latest Updates post.
     * @param commentMessage - message to post in comment.
     * @param postMessage - String used to generate post message locator.
     */
	public void commentOnLatestUpdatesPost(String commentMessage,String postMessage) {

		switchToFrame(findElement(By.xpath(HomepageUIConstants.latestUpdatesComment_iFrame.replace("PLACEHOLDER1", postMessage))));
		typeWithDelayWd(commentMessage, By.xpath(HomepageUIConstants.discoverCommentBody));
		driver.switchToFrame().returnToTopFrame();	
	}

}


