package com.ibm.conn.auto.util;

import com.ibm.atmn.waffle.extensions.user.User;

public class Mentions {
	
	private String beforeMentionText;
	private String afterMentionText;
	private String userUUID;
	private String browserURL;
	private User userToMention;
	
	public static class Builder {
		private String beforeMentionText = "";
		private String afterMentionText = "";
		private String userUUID;
		private String browserURL;
		private User userToMention;

		public Builder(User userToMention, String userUUID){
			this.userToMention = userToMention;
			this.userUUID = userUUID;
		}

		public Builder beforeMentionText(String beforeMentionText){
			this.beforeMentionText = beforeMentionText;
			return this;
		}
		
		public Builder afterMentionText(String afterMentionText){
			this.afterMentionText = afterMentionText;
			return this;
		}
		
		public Builder browserURL(String browserURL){
			this.browserURL = browserURL;
			return this;
		}

		public Mentions build() {
			return new Mentions(this);
		}
				
	}
	
	private Mentions(Builder builder) {
		this.setBrowserURL(builder.browserURL);
		this.setBeforeMentionText(builder.beforeMentionText);
		this.setUserToMention(builder.userToMention);
		this.setUserUUID(builder.userUUID);
		this.setAfterMentionText(builder.afterMentionText);
	}

	public String getBrowserURL(){
		return browserURL;
	}

	public void setBrowserURL(String browserURL){
		this.browserURL = browserURL;
	}

	public String getBeforeMentionText(){
		return beforeMentionText;
	}

	public void setBeforeMentionText(String beforeMentionText){
		this.beforeMentionText = beforeMentionText;
	}

	public User getUserToMention(){
		return userToMention;
	}

	public void setUserToMention(User userToMention){
		this.userToMention = userToMention;
	}

	public String getUserUUID(){
		return userUUID;
	}

	public void setUserUUID(String userUUID){
		this.userUUID = userUUID;
	}

	public String getAfterMentionText(){
		return afterMentionText;
	}

	public void setAfterMentionText(String afterMentionText){
		this.afterMentionText = afterMentionText;
	}

}
