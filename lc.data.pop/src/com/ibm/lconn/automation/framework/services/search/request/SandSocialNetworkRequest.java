package com.ibm.lconn.automation.framework.services.search.request;

import com.ibm.lconn.automation.framework.services.search.data.SocialNetworkType;


public class SandSocialNetworkRequest extends SandSocialPathRequest{
	
	private SocialNetworkType type;
	
	public SandSocialNetworkRequest()  {
		super();
	}

	public SocialNetworkType getType() {
		return type;
	}


	public void setType(SocialNetworkType type) {
		this.type = type;
	}

}

