import unittest
import sys, os
import re
import java

lc_admin_dir = os.environ.get("IC_ADMIN_DIR", "bin_lc_admin")
if not lc_admin_dir in sys.path:
    sys.path.append(lc_admin_dir)
if not globals().has_key('OAuthApplicationRegistrationService'):
    execfile(os.path.join(lc_admin_dir, 'oauthAdmin.py'))

class OAuthApplicationRegistrationServiceTest(unittest.TestCase):
	
	appID = "test_application"
	appName = "Sample Application"
	redirectURI = "http://www.test.com/oauth/redirect"
	
	def setUp(self):
		pass
	
	def test_addApplication(self):
		#prints: "An application was added with the new id "+self.appID+" ."
		result = OAuthApplicationRegistrationService.addApplication(self.appID,self.appName,self.redirectURI)
		self.assertEqual(self.appID,result)
		OAuthApplicationRegistrationService.deleteApplication(self.appID)
		
	def test_getApplicationById(self):
		tempID = "temp_test"
		OAuthApplicationRegistrationService.addApplication(tempID,self.appName,self.redirectURI)
		result = OAuthApplicationRegistrationService.getApplicationById(tempID)
		self.assertEqual(tempID, result["client_id"])
		OAuthApplicationRegistrationService.deleteApplication(tempID)
		
	def test_getApplicationsByIds(self):
		tempID = "temp_test"
		OAuthApplicationRegistrationService.addApplication(tempID,self.appName,self.redirectURI)
		OAuthApplicationRegistrationService.addApplication(self.appID,self.appName,self.redirectURI)
		result = OAuthApplicationRegistrationService.getApplicationById(tempID)
		result2 = OAuthApplicationRegistrationService.getApplicationById(self.appID)
		self.assertEqual(tempID, result["client_id"])
		self.assertEqual(self.appID, result2["client_id"])
		OAuthApplicationRegistrationService.deleteApplication(tempID)
		OAuthApplicationRegistrationService.deleteApplication(self.appID)
		
	def test_getAppicationByNonExistentId(self):
		result = OAuthApplicationRegistrationService.getApplicationById("does_not_exist")
		self.assertEquals(result,None)
	
	def test_editApplication(self):
		#prints: "The application with the id "+self.appID+" was updated successfully."
		OAuthApplicationRegistrationService.addApplication("temp_test",self.appName,self.redirectURI)
		newAppName = "Edit test"
		newRedirectURI = "http://www.test.com/edited/oauth/redirect"
		OAuthApplicationRegistrationService.editApplication("temp_test",newAppName,newRedirectURI)
		result = OAuthApplicationRegistrationService.getApplicationById("temp_test")
		self.assertEqual(newAppName, result["display_name"])
		self.assertEqual(newRedirectURI, result["redirect_uri"])
		OAuthApplicationRegistrationService.deleteApplication("temp_test")
		#returns nothing
		
	def test_editNonExistentApplication(self):
		flag = 0
		
		try:
			OAuthApplicationRegistrationService.editApplication("does","not","exist")
		except:
			flag = 1
			
		self.assertEqual(flag, 1)
		
	def test_getApplicationCount(self):
		OAuthApplicationRegistrationService.addApplication("test1",self.appName,self.redirectURI)	
		OAuthApplicationRegistrationService.addApplication("test2",self.appName,self.redirectURI)	
		OAuthApplicationRegistrationService.addApplication("test3",self.appName,self.redirectURI)	
		self.assert_(OAuthApplicationRegistrationService.getApplicationCount() >= 3)
		OAuthApplicationRegistrationService.deleteApplication("test1")
		OAuthApplicationRegistrationService.deleteApplication("test2")
		OAuthApplicationRegistrationService.deleteApplication("test3")
		#returns count
		
	def test_browseApplications(self):
		OAuthApplicationRegistrationService.addApplication(self.appID,self.appName,self.redirectURI)
		OAuthApplicationRegistrationService.addApplication("temp",self.appName,self.redirectURI)
		result = OAuthApplicationRegistrationService.browseApplications()
		self.assertEqual(OAuthApplicationRegistrationService.getApplicationCount(),len(result))
		OAuthApplicationRegistrationService.deleteApplication(self.appID)
		OAuthApplicationRegistrationService.deleteApplication("temp")
		
	def test_deleteApplication(self):
		flag = 0
		
		OAuthApplicationRegistrationService.addApplication(self.appID,self.appName,self.redirectURI)
		try:
			OAuthApplicationRegistrationService.deleteApplication(self.appID)
		except:
			flag = 1
			
		self.assertEqual(flag, 0)
		#Returns nothing
		
	def test_deleteNonExistentApplication(self):
		flag = 0
		
		try:
			OAuthApplicationRegistrationService.deleteApplication("doesNotExist")
		except:
			flag = 1
			
		self.assertEqual(flag, 1)
		
def suite():
	suite = unittest.TestSuite()
	suite.addTest(unittest.makeSuite(OAuthApplicationRegistrationServiceTest))
	return suite
	
if __name__ == '__main__':
	suite = unittest.TestLoader().loadTestsFromTestCase(OAuthApplicationRegistrationServiceTest)
    result = unittest.TextTestRunner(verbosity=2).run(suite)
    if len(result.errors) > 0 or len(result.failures) > 0:
        sys.exit(1)
