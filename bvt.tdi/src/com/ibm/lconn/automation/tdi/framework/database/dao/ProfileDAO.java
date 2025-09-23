package com.ibm.lconn.automation.tdi.framework.database.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.ibm.lconn.automation.tdi.framework.database.data.ProfileMapper;
import com.ibm.lconn.automation.tdi.framework.database.model.Profile;

public class ProfileDAO {
	
	private SqlSessionFactory sqlSessionFactory;
	
	public ProfileDAO(String dbPropertiesFile) {
		sqlSessionFactory = ConnectionFactory.getSqlSessionFactory(dbPropertiesFile, ProfileMapper.class);
	}
	
	public Profile getPerson(String uid) {
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ProfileMapper mapper = session.getMapper(ProfileMapper.class);
			Profile p = mapper.getPerson(uid);
			return p;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			session.close();
		}
	}
}
