package com.ibm.lconn.automation.tdi.framework.database.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import com.ibm.lconn.automation.tdi.framework.Util.PropertiesInstance;

public class ConnectionFactory {
	
	private static Map<Class<?>, SqlSessionFactory> factories = new HashMap<Class<?>, SqlSessionFactory>();

	public static SqlSessionFactory getSqlSessionFactory(String dbPropertiesFile, Class<?> mapper) {
		if(factories.get(mapper) != null){
			return factories.get(mapper);
		}
		
		Properties dbProp = PropertiesInstance.getInstance(dbPropertiesFile);
		DataSource dataSource = getDataSource(dbProp);
		
		TransactionFactory trxFactory = new JdbcTransactionFactory();
		
		Environment env = new Environment("dev", trxFactory, dataSource);
		Configuration config = new Configuration(env);
		config.addMapper(mapper);
		
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(config);
		factories.put(mapper, factory);
		
		return factory;
	}
	
	private static DataSource getDataSource(Properties p) {
		BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(p.getProperty("DB_DRIVER"));
        dataSource.setUrl(p.getProperty("DB_URL"));
        dataSource.setUsername(p.getProperty("DB_USER"));
        dataSource.setPassword(p.getProperty("DB_PASSWORD"));
        return dataSource;
	}
}
