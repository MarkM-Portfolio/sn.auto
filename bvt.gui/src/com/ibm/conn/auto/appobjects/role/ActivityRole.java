package com.ibm.conn.auto.appobjects.role;

import com.ibm.conn.auto.appobjects.Role;


public enum ActivityRole implements Role {
	
	OWNER("Owner"),
	AUTHOR("Author"),
	READER("Reader");
	
    public String name;
    private ActivityRole(String brand){
            this.name = brand;
    }
    
    @Override
    public String toString(){
            return name;
    }

}
