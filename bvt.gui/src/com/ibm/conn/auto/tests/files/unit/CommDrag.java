package com.ibm.conn.auto.tests.files.unit;

import java.util.List;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.webui.ICBaseUI;


public class CommDrag extends ICBaseUI {

	public CommDrag(RCLocationExecutor driver) {
		super(driver);
	}
	
	protected static Logger log = LoggerFactory.getLogger(CommDrag.class);
	
	public void DragAndDrop(Element source, Element target)
	{
		
		Actions acts = new Actions(source.getWebDriverExecutor().wd());
		try{
            Action action; 
            WebElement Source = source.getWebElement();
            WebElement des = target.getWebElement();
            acts.clickAndHold(Source);
            acts.moveToElement(des, 0, 0);
            acts.release();
            action = acts.build();  
            action.perform();
		}
		catch (StaleElementReferenceException se) {
            throw se;
		}
	}
	
	public Element GetFileElement(BaseFile file) {
		
        Element getFile = null;
		log.info("INFO: Locate the More link associated with our file");
		List<Element> files = driver.getVisibleElements("css=div[dojoattachpoint='streamNode'] tr[dndtype='file']");
		String fileName;
		
		if (file.getRename() == null) {
			fileName = file.getName() + file.getExtension();
		} else {
			fileName = file.getRename() + file.getExtension();
		}

		if(files.size() > 0) {
			for(Element fileElement : files){
			    if(fileElement.getText().contains(fileName)){
			        log.info("INFO: Select more link");
				    getFile =fileElement;
				    break;
			    }
		    }
		} 
		else {
			Assert.fail("No file elements were found on the page");
		}
		return getFile;
	}
	
	public String fileSpecificCheckmark(int position) {
		if (driver.getCurrentUrl().contains("files")){
			return "css=input[id='list_" + position + "']";
		}
		else if (driver.getCurrentUrl().contains("communities")){
			return "css=input[id='list_0_" + position + "']";
		} 
		else {
		  Assert.fail("File is neither a regular file nor a community file. This should never happen.");
			return null;
		}
	}
}
