package com.ibm.conn.auto.appobjects.role;

import com.ibm.conn.auto.appobjects.Role;

public enum WikiRole implements Role {
	
	EDITOR("Editor"),
	OWNER("Owner"),
	READER("Reader");
	
    public String name;
    private WikiRole(String brand){
            this.name = brand;
    }
    
    @Override
    public String toString(){
            return name;
    }

}
