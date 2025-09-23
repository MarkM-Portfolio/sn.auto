package com.ibm.conn.auto.appobjects.base;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.appobjects.BaseStateObject;



public class BaseSurveyQuestion implements BaseStateObject{

	
	public enum Type {
		MULTIPLECHOICE_ONEANSWER("Multiple choice: One answer only"),
		MULTIPLECHOICE_MULTIANSWER("Multiple choice: Multiple answers"),
		DROPDOWN("Drop-down menu"),
		TEXT_ONELINE("Text answer: One line"),
		TEXT_MULTILINE("Text answer: Multiple lines"),
		NUMBER("Number"),
		DATE("Date"),
		CURRENCY("Currency"),
		CHECKBOX("Check Box");	

		String option = null;
	
		Type(String option){
			this.option = option;
		}
		
		public String getOption(){
			return this.option.toString();
		}
		
	}

	public static class Option {
	    public String display;
	    public String saved;

	    //constructor
	    public Option(String display, String saved) {
	    	this.display = display;
	        this.saved = saved;
	    }
	}
	
	
	private String question;
	private Type questionType;
	private List<Option> options;

	
	public static class Builder {
		private String question;
		private Type questionType;	
		private List<Option> options = new ArrayList<Option>();
		
		public Builder (String question, Type questionType){
			this.question = question;
			this.questionType = questionType;
		}
		
		public Builder addOptions(List<Option> options) {
			this.options.addAll(options);
			return this;
		}
		
		public Builder addOption(Option option) {
			this.options.add(option);
			return this;
		}
				
		public BaseSurveyQuestion build() {
			return new BaseSurveyQuestion(this);
		}
	}
	
	private BaseSurveyQuestion(Builder b) {
			this.setQuestion(b.question);	
			this.setQuestionType(b.questionType);
			this.setOptions(b.options);
	 }
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}
	
	public Type getQuestionType() {
		return questionType;
	}

	public void setQuestionType(Type questionType) {
		this.questionType = questionType;
	}
	
	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
	public void addOption(Option option){
		this.options.add(option);
	}
}
