package com.ibm.lconn.automation.framework.services.common;

public class ProfileData {

	private String _email;
	private String _key;
	private String _sUser;
	private String _password;
	private String _userId;
	private String _realName;
	private String _org;
	private boolean _isAdmin;
	private boolean _isCurrentUser;
	private boolean _isModerator;
	private boolean _isConnectionsAdmin;
	
	public ProfileData(String username, String userpassword, String useremail, String userid, String userkey, String realName, boolean useSSL, boolean isAdmin, boolean isCurrentUser, boolean isModerator, boolean isConnectionsAdmin, String orgname){
		
		_sUser = username;
		_password = userpassword;
		_email = useremail;
		_key  = userkey;
		_userId  = userid;
		_realName = realName;
		_isAdmin = isAdmin;			
		_isCurrentUser = isCurrentUser;
		_isModerator =isModerator;
		_isConnectionsAdmin = isConnectionsAdmin;
		_org = orgname;
	}
	
	
	public String getUserId() {
		return _userId;
	}
	
	public void setUserId( String userid) {
		_userId = userid;
	}

	public String getEmail(){
		return _email;
	}
	
	public String getKey() {
		return _key; 
	}
	
	public void setKey(String key) {
		this._key = key;
	}
	
	public String getUserName(){
		return _sUser;
	}
	
	public String getPassword(){
		return _password;
	}
	
	public String getRealName(){
		return _realName;
	}
	
	public void setRealName(String realname){
		this._realName = realname;
	}
	
	public String getOrg(){
		return _org;
	}
	
	public boolean equals(ProfileData other){
		return(other.getKey().equals(_key));
	}
	
	public boolean isAdmin(){
		return _isAdmin;
	}
	
	public boolean isCurrentUser(){
		return _isCurrentUser;
	}
	
	public boolean isModerator(){
		return _isModerator;
	}
	
	public boolean isConnectionsAdmin(){
		return _isConnectionsAdmin;
	}
	
	
}
