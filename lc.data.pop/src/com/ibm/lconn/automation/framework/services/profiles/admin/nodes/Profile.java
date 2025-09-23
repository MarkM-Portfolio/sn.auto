package com.ibm.lconn.automation.framework.services.profiles.admin.nodes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Content.Type;

import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

public class Profile extends LCEntry {

	private HashMap<String, ProfileAttribute> profileAttributes;
	
	public Profile(HashMap<String, Element> editableFields) {
		profileAttributes = new HashMap<String, ProfileAttribute>();
		for(String key : editableFields.keySet()) {
			profileAttributes.put(key, new ProfileAttribute(key, null, null));
		}
	}

	public Profile(Entry entry) {
		super(entry);
		
		profileAttributes = new HashMap<String, ProfileAttribute>();
		
		Content content = entry.getContentElement();
		Element personAttributeElement = content.getFirstChild().getFirstChild();
		for(Iterator<Element> entryIterator = personAttributeElement.iterator(); entryIterator.hasNext();) {
			List<Element> elements = entryIterator.next().getElements();
			String key = elements.get(0).getText();
			
			List<Element> valueList = elements.get(1).getElements();
			profileAttributes.put(key, new ProfileAttribute(key, valueList.get(0).getText(), valueList.get(1).getText()));

		}
	}
	
	@Override
	public Entry toEntry() {
		Factory factory = Abdera.getNewFactory();

		Element[] extensions = { };
		Category[] categories = { };
		
		Entry entry = createBasicEntry(extensions, categories);
		
		Content content = factory.newContent();
		content.setContentType(Type.typeFromString("application/xml"));
		ExtensibleElement personElement = factory.newElement(new QName("http://ns.opensocial.org/2008/opensocial", "person"));
		ExtensibleElement attribElement = factory.newElement(new QName("http://ns.opensocial.org/2008/opensocial", "com.ibm.snx_profiles.attrib"));
		for(ProfileAttribute attrib : profileAttributes.values()) {
			attribElement.addExtension(attrib.toEntry());
		}
		
		personElement.addExtension(attribElement);
		content.setValueElement(personElement);
		
		entry.setContentElement(content);
		
		return entry;
	}

	public HashMap<String, ProfileAttribute> getProfileAttributes() {
		return profileAttributes;
	}
	
	public ProfileAttribute getAttribute(String attributeName) {
		return profileAttributes.get(attributeName);
	}
	
	public void setAttribute(String attributeName, ProfileAttribute attr) {
		profileAttributes.put(attributeName, attr);
	}
}
