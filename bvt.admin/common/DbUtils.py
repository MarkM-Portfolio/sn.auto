#  Jython Script to perform database operations.
#  (C) Copyright IBM Corporation, 2009

# AUTHOR:           yees@us.ibm.com
# INITIAL RELEASE:  LC2.5

from java.sql import *
from java.lang import Class
from java.lang import Boolean
import sys

class DBUtils:
	#  Note: Cannot add db drivers using the wsadmin_classpath option because of jython bug - http://bugs.jython.org/issue1127. 
	#  Until it's fixed, need to unzip the drivers to <was_dir>/optionalLibraries/jython/Lib.    These are the different workaround tried to no avail.
	#   Class.forName('COM.ibm.db2.jdbc.app.DB2Driver', 1, Thread.currentThread().getContextClassLoader()).newInstance()
	#   from COM.ibm.db2.jdbc.app import DB2Driver
	#   driver = DB2Driver()
	#  DriverManager.registerDriver(driver)

	# Class constructor
	# Input: component = the component prefix in property names whose database to connect.  eg. people, oa
    def __init__(self, component):
      self.__dbType = java.lang.System.getProperty("db.type").lower()
      self.__hostname = java.lang.System.getProperty("db.hostname")
      self.__dbName = java.lang.System.getProperty("db." + component + ".name")
      self.__dbPort = java.lang.System.getProperty("db." + component + ".port")
      self.__dbUserName = java.lang.System.getProperty("db." + component + ".username")
      self.__dbUserPassword = java.lang.System.getProperty("db." + component + ".password")

    # This method connects to a given database
    def dbConnect(self):
	   if self.__dbType == "db2":
	     Class.forName("COM.ibm.db2.jdbc.app.DB2Driver").newInstance()
	     jdbcUrl = "jdbc:" + self.__dbType + ":" + self.__dbName;
	   elif self.__dbType == "sqlserver":
	     Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance()
	     jdbcUrl = "jdbc:" + self.__dbType + "://" + self.__hostname + ":" + self.__dbPort + ";databaseName=" + self.__dbName;
	   elif self.__dbType == "oracle":
	     Class.forName("oracle.jdbc.driver.OracleDriver").newInstance()
	     jdbcUrl = "jdbc:oracle:thin:@" + self.__hostname + ":" + self.__dbPort + ":" + self.__dbName;
		 
	   try:
	     self.__dbConn = DriverManager.getConnection(jdbcUrl, self.__dbUserName, self.__dbUserPassword)
	     self.__stmt = None		 
	   except:
	     print "==>Error! - Database connection cannot be established!"
	     traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])

		 
    # This method disconnects a given database.
    def dbDisconnect(self):
	   try:
	     if self.__stmt != None:
	        self.__stmt.close()
			
	     self.__dbConn.close()
	   except:
	     print "==>Error! - Cannot disconnect database!"
	     traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])	    


    def commit(self):
	   self.__dbConn.commit()
	   
		 
    # This method create a Statement object for the class.
    # Input:  isReadOnly - boolean to indicate whether the resultset should be read only.
    def createStatement(self, isReadOnly):		 
	   if isReadOnly == Boolean.TRUE:
	     concur = ResultSet.CONCUR_READ_ONLY
	   else:
	     concur = ResultSet.CONCUR_UPDATABLE 
		 
	   try:
	     self.__stmt = self.__dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, concur)
	   except:
	     print "==>Error! - Connection.createStatement(...) failed!"
	     traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])

		 
    # This method add the given SQL to the batch queries.  Need to call createStatement(...) first if not already.
    # Input: sql - SQL to execute.
    def addToBatchQueries(self, sql):
	   try:   
	     rs = self.__stmt.executeBatch(sql)
	   except:
	     print "==>Error! - Cannot execute query " + sql
	     traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])

	   return rs

	   
    # This method executes a batch of queries and returns the resultset.
    # Input: sql - SQL to execute.
    def runBatchQueries(self, sql):
	   try:   
	     rs = self.__stmt.executeBatch(sql)
	   except:
	     print "==>Error! - Cannot execute query " + sql
	     traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])

	   return rs		 
		 
		 
    # This method executes a single query and returns the resultset.  Need to call createStatement(...) first if not already. 
    # Input: sql - SQL to execute.
    def runSingleQuery(self, sql):
	   try:   
	     rs = self.__stmt.executeQuery(sql)
	   except:
	     print "==>Error! - Cannot execute query " + sql
	     traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])

	   return rs
	   

    # This method sends a SQL for precompilation.
    # Input: sql - SQL to execute.
    # Input: isReadOnly - boolean to indicate whether the resultset should be read only.
    # Output: prepStmt - precompiled SQL statement
    def prepareQuery(self, sql, isReadOnly):
	   if isReadOnly == Boolean.TRUE:
	     concur = ResultSet.CONCUR_READ_ONLY
	   else:
	     concur = ResultSet.CONCUR_UPDATABLE    

	   try:
	     prepStmt = self.__dbConn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, concur)
	   except:
	     print "==>Error! - Cannot run precompile SQL - \n" + sql
	     traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])

	   return prepStmt

    # This method executes a given precompiled query and returns the resultset.
    # Input: PreparedStatement object
    # Output: rs - result set.
    def runPreparedQuery(self, prepStmt):
	   try:
	     rs = prepStmt.executeQuery()
	     return rs        
	   except:
	     print "==>Error! - Cannot execute query!"
	     traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])

	   
    # This method executes a given precompiled INSERT, UPDATE or DELETE statement.
    # Input: PreparedStatement object
    # Output: rs - result set.
    def runPreparedUpdate(self, prepStmt):
	   try:    
	     result = prepStmt.executeUpdate()
	     return result	   
	   except:
	     print "==>Error! - Cannot execute update!"
	     traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])



# pplDbUtils = DBUtils("peopleDBName")
# pplDbUtils.dbConnect()
# pplDbUtils.dbDisconnect()

