package com.hcl.lconn.automation.framework.payload;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BasePayload {
	
	private static Logger log = LoggerFactory.getLogger(BasePayload.class);
	public Map<String, Object> additionalProperties = new HashMap<>();
	
	// Capture all other fields that Jackson do not match other members
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }
    
    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
    	additionalProperties.put(name, value);
    }

	
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException jpe)  {
			log.error(jpe.getMessage());
			return null;
		}
	}

}
