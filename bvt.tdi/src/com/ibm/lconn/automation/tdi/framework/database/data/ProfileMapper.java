package com.ibm.lconn.automation.tdi.framework.database.data;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.ibm.lconn.automation.tdi.framework.database.model.Profile;

public interface ProfileMapper {
	
	final String SELECT_STATUS_BY_ID = "SELECT * FROM EMPINST.EMPLOYEE WHERE PROF_UID = #{uid}";
	
	@Select(SELECT_STATUS_BY_ID)
	@Results(value = {
		@Result(property="uid", column="PROF_UID"),
		@Result(property="givenName", column="PROF_GIVEN_NAME"),
		@Result(property="state", column="PROF_STATE")
	})
	Profile getPerson(String uid);
}
