package com.ibm.conn.auto.appobjects.role;

import com.ibm.conn.auto.appobjects.Role;

public enum BlogRole implements Role {

	OWNER("Owner"),
	AUTHOR("Author"),
	DRAFT("Draft");
	
    public String name;
    private BlogRole(String brand){
            this.name = brand;
    }
    
    @Override
    public String toString(){
            return name;
    }
}
