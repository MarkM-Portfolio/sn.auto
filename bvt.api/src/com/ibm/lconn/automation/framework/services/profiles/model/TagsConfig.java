/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2012, 2013                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.automation.framework.services.profiles.model;

import java.util.HashMap;
import java.util.Map;
import org.apache.abdera.model.Element;
import org.testng.Assert;
import com.ibm.lconn.automation.framework.services.profiles.util.ApiConstants;

/**
 * The document that introspects supported tagging behavior on application.
 */
public class TagsConfig extends AtomResource {

	private Map<String, TagConfig> tagConfigs;

	public TagsConfig(Element e) throws Exception {
		// validate we have the right element
		Assert.assertEquals(e.getQName(), ApiConstants.TagConfigConstants.TAGS_CONFIG);
		// iterate over children and create connectionTypes
		tagConfigs = new HashMap<String, TagConfig>();
		for (Element child : e.getElements()) {
			TagConfig tagConfig = new TagConfig(child);
			tagConfig.validate();
			tagConfigs.put(tagConfig.getType(), tagConfig);
		}
	}

	public Map<String, TagConfig> getTagConfigs() {
		return tagConfigs;
	}

	public TagsConfig validate() throws Exception {
		for (TagConfig o : tagConfigs.values()) {
			o.validate();
		}
		return this;
	}

}
