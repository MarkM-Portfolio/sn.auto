package com.ibm.conn.auto.appobjects.base;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ICBaseUI;
import com.ibm.conn.auto.webui.SurveysUI;

public class BaseSurvey implements BaseStateObject{
	
	private String name;
	private String description;
	private boolean anonResponse;
	private List<BaseSurveyQuestion> questions;

	
	public static class Builder {
		private String name;
		private String description;		
		private List<BaseSurveyQuestion> questions = new ArrayList<BaseSurveyQuestion>();
		private boolean anonResponse = false;
		
		public Builder (String name){
			this.name = name;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Builder questions(List<BaseSurveyQuestion> questions){
			this.questions = questions;
			return this;
		}
		
		public Builder anonResponse(boolean anonResponse){
			this.anonResponse = anonResponse;
			return this;
		}
		
		public BaseSurvey build() {
			return new BaseSurvey(this);
		}
	}
	
	private BaseSurvey(Builder b) {
			this.setName(b.name);	
			this.setDescription(b.description);
			this.setAnonResponse(b.anonResponse);
			this.setQuestions(b.questions);
	 }
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean getAnonResponse(){
		return anonResponse;
	}
	
	public void setAnonResponse(boolean anonResponse){
		this.anonResponse = anonResponse;
	}
	
	public List<BaseSurveyQuestion> getQuestions(){
		return questions;
	}
	
	public void setQuestions(List<BaseSurveyQuestion> questions){
		this.questions = questions;
	}
	
	public void create(CommunitiesUI ui){
		ui.createSurvey(this);
	}
	
	public void createUsingSurveyUI(SurveysUI ui){
		ui.createSurvey(this);
	}
	
	public void delete(ICBaseUI ui){
		
	}
	
}
