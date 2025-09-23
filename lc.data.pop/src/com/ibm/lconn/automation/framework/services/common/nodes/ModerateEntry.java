package com.ibm.lconn.automation.framework.services.common.nodes;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

/**
 * For moderation - approval or review
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class ModerateEntry extends LCEntry {

	private Entry parent;
	private String mod_id;

	private String ref_item_type;
	private String mod_action;

	//private boolean isApproval;
	
	public ModerateEntry(String content, String inReplyTo, String ref_item_type, String mod ) {
		super();
		setParent(parent);
		setContent(content);
		setModId(inReplyTo);
		setRefItemType(ref_item_type); // comment, forum-reply, forum-topic
		setModAction(mod);  // approve, reject; dismiss, quarantine, restore
	}

	public ModerateEntry(Entry entry) {
		super(entry);
	}
	
	@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		
		Element snx_inrefto = getFactory().newElement(StringConstants.SNX_INREFTO);		
		snx_inrefto.setAttributeValue("rel", StringConstants.REL_REPORT_ITEM);
		snx_inrefto.setAttributeValue(StringConstants.REF_ITEM_TYPE, getRefItemType());
		snx_inrefto.setAttributeValue("ref", getModId());
		
		Element snx_moderation = getFactory().newElement(StringConstants.SNX_MODERATION);
		snx_moderation.setAttributeValue(StringConstants.ATTR_ACTION, getModAction());
		
		entry.addExtension((Element) snx_inrefto);
		entry.addExtension((Element) snx_moderation);
		
		entry.setContent(getContent());

		return entry;	
	}
	
	public Entry getParent() {
		return parent;
	}

	public void setParent(Entry parent) {
		this.parent = parent;
	}
	
	public String getModId() {
		return mod_id;
	}

	public void setModId(String mod_id) {
		this.mod_id = mod_id;
	}
	
	public String getRefItemType() {
		return ref_item_type;
	}

	public void setRefItemType(String ref_item_type) {
		this.ref_item_type = ref_item_type;
	}
	
	public String getModAction() {
		return mod_action;
	}

	public void setModAction(String mod) {
		this.mod_action = mod;
	}
}
