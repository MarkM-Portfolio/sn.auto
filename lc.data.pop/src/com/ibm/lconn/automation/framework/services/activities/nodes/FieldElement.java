package com.ibm.lconn.automation.framework.services.activities.nodes;

import java.util.Date;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.StringConstants.FieldType;

public class FieldElement {
	private Factory factory;

	private String fid;
	private boolean hidden;
	private String name;
	private int position;
	private FieldType fieldType;
	private Element[] contentElements;
	private String text;

	public FieldElement(String fid, boolean hidden, String name, int position, FieldType fieldType, Element[] contentElements, String text) {
		setFactory(Abdera.getNewFactory());
		setFid(fid);
		setHidden(hidden);
		setName(name);
		setPosition(position);
		setFieldType(fieldType);
		setContent(contentElements);
		setText(text);
	}
	
	public FieldElement(Element field) {
		setFactory(Abdera.getNewFactory());
		setFid(field.getAttributeValue(StringConstants.ATTR_FID));
		setHidden(Boolean.valueOf(field.getAttributeValue(StringConstants.ATTR_HIDDEN)).booleanValue());
		setName(field.getAttributeValue(StringConstants.ATTR_NAME));
		setPosition(Integer.parseInt(field.getAttributeValue(StringConstants.ATTR_POSITION)));
		setFieldType(Enum.valueOf(FieldType.class, field.getAttributeValue(StringConstants.ATTR_TYPE).toUpperCase()));
		setText(field.getText());
		setContent(field.getElements());
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	public Element[] getContent() {
		return contentElements;
	}

	public void setContent(Element[] content) {
		this.contentElements = content;
	}
	

	private void setContent(List<Element> elements) {
		Element[] elementList = new Element[elements.size()];
		for(int i = 0; i < elements.size(); i++) {
			elementList[i] = elements.get(i);
		}
		setContent(elementList);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public Element toElement() {
		ExtensibleElement fieldElement = getFactory().newExtensionElement(StringConstants.SNX_FIELD);
		fieldElement.setAttributeValue("fid", getFid());
		fieldElement.setAttributeValue("hidden", String.valueOf(isHidden()));
		fieldElement.setAttributeValue("name", getName());
		fieldElement.setAttributeValue("position", String.valueOf(getPosition()));
		fieldElement.setAttributeValue("type", String.valueOf(getFieldType()).toLowerCase());
		fieldElement.setText(getText());
		
		if(getContent() != null) {
			for(Element element : getContent()) {
				fieldElement.addExtension(element);
			}
		}
		
		return fieldElement;
	}
	
	@Override
	public String toString() {
		return toElement().toString();
	}
	
	public Factory getFactory() {
		return factory;
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}
	
	public void setPersonInfo(String name, String userid, String userstate) {
		Element nameElement = getFactory().newName();
		nameElement.setText(name);
		Element useridElement = getFactory().newElement(StringConstants.SNX_USERID);
		useridElement.setText(userid);
		Element userstateElement = getFactory().newElement(StringConstants.SNX_USER_STATE);
		userstateElement.setText(userstate);
		
		setContent(new Element[] { nameElement, useridElement, userstateElement });
	}
	
	public void setPersonInfo(Person person) {
		setFieldType(FieldType.PERSON);
		setContent(new Element[] { person.getNameElement(), person.getExtension(StringConstants.SNX_USERID), person.getExtension(StringConstants.SNX_USER_STATE)});
	}
	
	public void setAttachmentInfo() {
		setName(StringConstants.STRING_ATTACHMENT);
		setFieldType(FieldType.FILE);
		
	}
	
	public void setLinkToFileInfo(String href, String title) {
		setLink(href, title);
		setName(StringConstants.STRING_LINK_TO_FILE);
	}
	
	public void setLinkToFolderInfo(String href, String title) {
		setLink(href, title);
		setName(StringConstants.STRING_LINK_TO_FOLDER);
	}
	
	public void setLink(String href, String title) {
		setName(StringConstants.STRING_BOOKMARK);
		setFieldType(FieldType.LINK);
		
		Link link = getFactory().newLink();
		link.setHref(href);
		link.setTitle(title);
		
		setContent(new Element[] { link } );
	}
	
	public void setDateInfo(Date date) {
		setName(StringConstants.STRING_DATE);
		setFieldType(FieldType.DATE);
		setText(Utils.dateFormatter.format(date));
	}
}
