package com.ibm.conn.auto.webui.cnx8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;

public class FilesUICnx8 extends HCBaseUI {
	
	protected static Logger log = LoggerFactory.getLogger(FilesUICnx8.class);
	
	public FilesUICnx8(RCLocationExecutor driver) {
		super(driver);
	}
	
	/**
	 * This method will return a locator of file's Link/Thumbnail
	 * @param BaseFile - file
	 */
	public static String getFileLink(BaseFile file) {
		return "//*[@dndelementtitle='" + file.getName() + "']";
	}
	/**
	 * This method will return a locator of folder
	 * @param folderName
	 */
	public static String getFolder(String folderName) {
		return "//div[@dndtype='folder']//div[@name='title']/span[text()='"+folderName+"']";
	}
	/**
	 * This method return locator for image displayed on preview
	 * @param file
	 * @return
	 */
	public static String fileImageOnPreview(BaseFile file) {
		return "//img[contains(@src,'"+file.getRename()+file.getExtension()+"')]";
	}
	
	/**
	 * This method return locator for folder listed in grid view
	 * @param folder
	 * @return
	 */
	public static String getFolderFromGridView(String folderName) {
	   return "//table//div[starts-with(@class,'dojoDndSource')]//div[@dndelementtitle='"+folderName+"']";
	}

	/**
	 * This method return locator for file checkbox on add to pop up
	 * @param folder
	 * @return
	 */
	public static String getFolderCheckbox(String folderName) {
		return "//div[contains(@id,'TreeWithBreadcrumb')]//span[contains(@class,'lconnText')][text()='"+folderName+"']";
	}
	
	/**
	 * This method return locator for folder listed in personal view
	 * @param folder
	 * @return
	 */
	public static String getFolderFromPeronalView(BaseFolder folder) {
		return "//table//div[starts-with(@class,'dojoDndSource')]//div[@dndelementtitle='"+folder.getName()+"']";
	}
	
	/**
	 * This method return locator for folder action menu
	 * @param folder
	 * @return
	 */
	public static String getFolderBreadcrumb(BaseFolder folder) {
		return "div[class*='lconnBreadcrumbitem'] [title='"+folder.getName()+" folder action menu']";
	}
	
	/**
	 * This method return locator for edit dropdown of specified folders
	 * @param folder
	 * @return
	 */
	public static String getFolderActionMenuDropdown(BaseFolder folder) {
		return "//div[contains(@title,'"+folder.getName()+"')]/img";
	}
	
	/**
	 * This method return locator for breadcrumb node at specified position
	 * @param position
	 * @return
	 */
	public static String getBreadCrumbNode(int position) {
		return "//div[@dojoattachpoint='breadcrumbShownNodes']//li["+position+"]//div[@role='toolbar']//div[contains(@id,'lconn_files_action_title_more')]";
	}	

}
