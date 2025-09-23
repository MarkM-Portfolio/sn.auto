package com.ibm.conn.auto.appobjects.base;

import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Builder;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.IcecUI;

public class BaseHighlights implements BaseStateObject{

	private String tinyEditorFunctionalitytoRun;
	private String description;
	private String name;
	
	public static class Builder {
		private String description;
		private String tinyEditorFunctionalitytoRun;
		private String name;

		public Builder(String name){
			this.name = name;
		}
	
		public Builder tinyEditorFunctionalitytoRun(String functionality)
		{
			this.tinyEditorFunctionalitytoRun= functionality;
			return this;
		}
		
		public Builder description(String functionality)
		{
			this.description= functionality;
			return this;
		}
		
		public BaseHighlights build() {
			return new BaseHighlights(this);
		}
		
	}

	private BaseHighlights(Builder highlightsObj) {
				
		this.setDescription(highlightsObj.description);
		this.setTinyEditorFunctionalitytoRun(highlightsObj.tinyEditorFunctionalitytoRun);
		this.setName(highlightsObj.name);
	}
	
	public void setTinyEditorFunctionalitytoRun(String functionality)
	{
		this.tinyEditorFunctionalitytoRun = functionality;
	}
	
	public String getTinyEditorFunctionalitytoRun() {
		return tinyEditorFunctionalitytoRun;
	}
	
	public void setDescription(String functionality)
	{
		this.description = functionality;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setName(String functionality)
	{
		this.name = functionality;
	}
	
	public String getName() {
		return name;
	}
	
	public String verifyTinyEditor(IcecUI ui)  {
		String TEText = ui.verifyTinyEditor(this);
		return TEText;
	}

}
