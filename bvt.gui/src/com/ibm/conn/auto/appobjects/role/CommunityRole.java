package com.ibm.conn.auto.appobjects.role;

import com.ibm.conn.auto.appobjects.Role;

public enum CommunityRole implements Role {
	
	OWNERS("Owners"),
	MEMBERS("Members");
	
    public String name;
    private CommunityRole(String brand){
            this.name = brand;
    }
    
    @Override
    public String toString(){
            return name;
    }

}
