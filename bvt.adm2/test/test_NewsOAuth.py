import unittest
import sys, os
import re
import java

lc_admin_dir = os.environ.get("IC_ADMIN_DIR", "bin_lc_admin")
if not lc_admin_dir in sys.path:
    sys.path.append(lc_admin_dir)
if not globals().has_key('NewsOAuth2ConsumerService'):
    execfile(os.path.join(lc_admin_dir, 'newsAdmin.py'))

class NewsOAuth2ConsumerServiceTest(unittest.TestCase):
	
	providerName = "providerTest"
	redirectUri = "http://thisisatest.com"
	authUrl = "http://test.com/auth"
	tokenUrl = "http://test.com/token"
	
	def setUp(self):
		pass
	
	def test_registerProvider(self):
		#prints: "The provider with the name "+providerName+" is now registered."
		result = NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		self.assertEqual(self.providerName,result)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		
	def test_findProvider(self):
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		result = NewsOAuth2ConsumerService.findProvider(self.providerName)
		self.assertEqual(self.providerName, result["name"])
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		
	def test_findProviders(self):
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerProvider("test2","standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerProvider("test3","standard","true","false",self.authUrl,self.tokenUrl)
		result = NewsOAuth2ConsumerService.findProvider(self.providerName)
		result2 = NewsOAuth2ConsumerService.findProvider("test2") 
		result3 = NewsOAuth2ConsumerService.findProvider("test3") 
		self.assertEqual(self.providerName, result["name"])
		self.assertEqual("test2", result2["name"])
		self.assertEqual("test3", result3["name"])
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		NewsOAuth2ConsumerService.deleteProvider("test2")
		NewsOAuth2ConsumerService.deleteProvider("test3")
		
	def test_findNonExistentProvider(self):
		result = NewsOAuth2ConsumerService.findProvider("Does_not_exist")
		self.assertEqual(result,None)
		
	#Count multiple
	def test_countProvider(self):
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerProvider("test2","standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerProvider("test3","standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		self.assert_(NewsOAuth2ConsumerService.countProvider() >= 3)
		NewsOAuth2ConsumerService.deleteProvider("test3")
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		NewsOAuth2ConsumerService.deleteProvider("test3")
	
	def test_browseProviders(self):
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		result = NewsOAuth2ConsumerService.browseProvider(20,1)
		self.assert_(len(result) == NewsOAuth2ConsumerService.countProvider())
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		#Returns a list of objects
		
	def test_deleteProvider(self):
		# prints 1 if successful 0 if not
		NewsOAuth2ConsumerService.registerProvider("Temp","standard","true","false",self.authUrl,self.tokenUrl)
		result = NewsOAuth2ConsumerService.deleteProvider("Temp")
		self.assertEqual(result,1)
		
	def test_deleteNonExistentProvider(self):
		result = NewsOAuth2ConsumerService.deleteProvider("DoesNotExist")
		self.assertEqual(result,0)
		
	#################Client################################
	
	clientName = "client123"
	ctype = "confidential" #Can be either confidential or public
	grantType = "code" #Can be code or client_credentials
	clientID = "11234"
	clientSecret = "my-secret"
	redirectUri = "https://test/gadgets/oauth2callback"
	
	def test_registerClient(self):
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		result = NewsOAuth2ConsumerService.registerClient(self.clientName,self.providerName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		self.assertEqual(self.clientName,result)
		NewsOAuth2ConsumerService.deleteClient(self.clientName)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
	
	def test_countClient(self):
		clientName2 = "test2"
		clientName3 = "test3"
		
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerClient(self.clientName,self.providerName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		NewsOAuth2ConsumerService.registerClient(clientName2,self.providerName,self.ctype,self.grantType,"1111",self.clientSecret,self.redirectUri)
		NewsOAuth2ConsumerService.registerClient(clientName3,self.providerName,self.ctype,self.grantType,"1444",self.clientSecret,self.redirectUri)
		result = NewsOAuth2ConsumerService.countClient(self.providerName)
		self.assertEqual(result,3)
		NewsOAuth2ConsumerService.deleteClient(self.clientName)
		NewsOAuth2ConsumerService.deleteClient(clientName2)
		NewsOAuth2ConsumerService.deleteClient(clientName3)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		
	def test_findClient(self):
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerClient(self.clientName,self.providerName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		result = NewsOAuth2ConsumerService.findClient(self.clientName)
		self.assertEqual(result["clientId"],self.clientID)
		NewsOAuth2ConsumerService.deleteClient(self.clientName)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		
	def test_findClients(self):
		clientName2 = "test2"
		clientName3 = "test3"
		clientID2 = "22222"
		clientID3 = "11111"
		
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerClient(self.clientName,self.providerName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		NewsOAuth2ConsumerService.registerClient(clientName2,self.providerName,self.ctype,self.grantType,clientID2,self.clientSecret,self.redirectUri)
		NewsOAuth2ConsumerService.registerClient(clientName3,self.providerName,self.ctype,self.grantType,clientID3,self.clientSecret,self.redirectUri)
		result = NewsOAuth2ConsumerService.findClient(self.clientName)
		result2 = NewsOAuth2ConsumerService.findClient(clientName2)
		result3 = NewsOAuth2ConsumerService.findClient(clientName3)
		self.assertEqual(result["clientId"],self.clientID)
		self.assertEqual(result2["clientId"],clientID2)
		self.assertEqual(result3["clientId"],clientID3)
		NewsOAuth2ConsumerService.deleteClient(self.clientName)
		NewsOAuth2ConsumerService.deleteClient(clientName2)
		NewsOAuth2ConsumerService.deleteClient(clientName3)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		
	def test_findNonExistentClient(self):
		result = NewsOAuth2ConsumerService.findClient("DoesNotExist")
		self.assertEqual(result, None)
		
	def test_deleteClient(self):
		tempProviderName = "temp providerName"
		tempClientName = "temp clientName"
		NewsOAuth2ConsumerService.registerProvider(tempProviderName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerClient(tempClientName,tempProviderName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		result = NewsOAuth2ConsumerService.deleteClient(tempClientName)
		self.assertEqual(1,result)
		NewsOAuth2ConsumerService.deleteProvider(tempProviderName)
		
	def test_deleteNonExistentClient(self):
		result = NewsOAuth2ConsumerService.deleteClient("DoesNotExist")
		self.assertEqual(result,0)

	####################Gadget############################
	
	widgetID = "aad20aa1-c0fa-48ef-bd05-8abe630c0012"
	serviceName = "connections_service_test"
	
	def test_bindGadget(self):
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerClient(self.clientName,self.providerName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		result = NewsOAuth2ConsumerService.bindGadget(self.widgetID,self.serviceName,self.clientName,"false")
		self.assertEqual(self.widgetID,result)
		NewsOAuth2ConsumerService.unbindGadget(self.widgetID,self.serviceName)
		NewsOAuth2ConsumerService.deleteClient(self.clientName)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
	
	def test_browseGadgetBinding(self):
		#NewsOAuth2ConsumerService.browseGadgetBinding("aad20aa1-c0fa-48ef-bd05-8abe630c0012", "client123", 50, 1)
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerClient(self.clientName,self.providerName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		NewsOAuth2ConsumerService.bindGadget(self.widgetID,self.serviceName,self.clientName,"false")
		result = NewsOAuth2ConsumerService.browseGadgetBinding(self.widgetID, self.clientName, 50, 1)
		self.assert_(len(result) > 0)
		NewsOAuth2ConsumerService.unbindGadget(self.widgetID,self.serviceName)
		NewsOAuth2ConsumerService.deleteClient(self.clientName)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		#Returns a list
	
	def test_countGadgetBinding(self):
		serviceName2 = "Test"
		serviceName3 = "Also_a_Test"
		 
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerClient(self.clientName,self.providerName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		NewsOAuth2ConsumerService.bindGadget(self.widgetID,self.serviceName,self.clientName,"false")
		NewsOAuth2ConsumerService.bindGadget(self.widgetID,serviceName2,self.clientName,"false")
		NewsOAuth2ConsumerService.bindGadget(self.widgetID,serviceName3,self.clientName,"false")
		result = NewsOAuth2ConsumerService.countGadgetBinding(self.widgetID,self.clientName)
		self.assertEqual(result,3)
		NewsOAuth2ConsumerService.unbindGadget(self.widgetID,self.serviceName)
		NewsOAuth2ConsumerService.unbindGadget(self.widgetID,serviceName2)
		NewsOAuth2ConsumerService.unbindGadget(self.widgetID,serviceName3)
		NewsOAuth2ConsumerService.deleteClient(self.clientName)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		
	def test_findGadgetBindingByWidgetId(self):
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerClient(self.clientName,self.providerName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		NewsOAuth2ConsumerService.bindGadget(self.widgetID,self.serviceName,self.clientName,"false")
		result = NewsOAuth2ConsumerService.findGadgetBindingByWidgetId(self.widgetID, self.serviceName)
		self.assertEqual(result["widgetId"],self.widgetID)
		NewsOAuth2ConsumerService.unbindGadget(self.widgetID,self.serviceName)
		NewsOAuth2ConsumerService.deleteClient(self.clientName)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
	
	def test_findGadgetsBindingByWidgetIds(self):
		serviceName2 = "test"
		
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerClient(self.clientName,self.providerName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		NewsOAuth2ConsumerService.bindGadget(self.widgetID,self.serviceName,self.clientName,"false")
		NewsOAuth2ConsumerService.bindGadget(self.widgetID,serviceName2,self.clientName,"false")
		result = NewsOAuth2ConsumerService.findGadgetBindingByWidgetId(self.widgetID, self.serviceName)
		result2 = NewsOAuth2ConsumerService.findGadgetBindingByWidgetId(self.widgetID,serviceName2)
		self.assertEqual(result["widgetId"],self.widgetID)
		self.assertEqual(result2["widgetId"],self.widgetID)
		NewsOAuth2ConsumerService.unbindGadget(self.widgetID,self.serviceName)
		NewsOAuth2ConsumerService.unbindGadget(self.widgetID,serviceName2)
		NewsOAuth2ConsumerService.deleteClient(self.clientName)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		
	def test_cantFindGadgetBindingByWidgetId(self):
		result = NewsOAuth2ConsumerService.findGadgetBindingByWidgetId("Doesn't", "exist")
		self.assertEqual(result,None)
		
	def test_findGadgetBindingByUri(self):
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerClient(self.clientName,self.providerName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		NewsOAuth2ConsumerService.bindGadget(self.widgetID,self.serviceName,self.clientName,"false")
		temp = NewsOAuth2ConsumerService.findGadgetBindingByWidgetId(self.widgetID, self.serviceName)
		uri = temp["uri"]
		result = NewsOAuth2ConsumerService.findGadgetBindingByUri(uri, self.serviceName)
		self.assertEqual(result["uri"],temp["uri"])
		NewsOAuth2ConsumerService.unbindGadget(self.widgetID,self.serviceName)
		NewsOAuth2ConsumerService.deleteClient(self.clientName)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		
	def test_findGadgetsBindingByUris(self):
		serviceName2 = "test"
		
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerClient(self.clientName,self.providerName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		NewsOAuth2ConsumerService.bindGadget(self.widgetID,self.serviceName,self.clientName,"false")
		NewsOAuth2ConsumerService.bindGadget(self.widgetID,serviceName2,self.clientName,"false")
		temp = NewsOAuth2ConsumerService.findGadgetBindingByWidgetId(self.widgetID, self.serviceName)
		temp2 = NewsOAuth2ConsumerService.findGadgetBindingByWidgetId(self.widgetID, serviceName2)
		result = NewsOAuth2ConsumerService.findGadgetBindingByUri(temp["uri"], self.serviceName)
		result2 = NewsOAuth2ConsumerService.findGadgetBindingByUri(temp2["uri"], serviceName2)
		self.assertEqual(result["uri"],temp["uri"])
		self.assertEqual(result["uri"],temp2["uri"])
		NewsOAuth2ConsumerService.unbindGadget(self.widgetID,self.serviceName)
		NewsOAuth2ConsumerService.unbindGadget(self.widgetID,serviceName2)
		NewsOAuth2ConsumerService.deleteClient(self.clientName)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		
	def test_cantFindGagetBindingByUri(self):
		result = NewsOAuth2ConsumerService.findGadgetBindingByUri("doesn't.com/exist","NoName")
		self.assertEqual(result,None)
		
	def test_unbindGadget(self):
		#expectedResult = "The widget with id "+widgetID+" and "+serviceName+" is now removed."
		NewsOAuth2ConsumerService.registerProvider(self.providerName,"standard","true","false",self.authUrl,self.tokenUrl)
		NewsOAuth2ConsumerService.registerClient(self.clientName,self.providerName,self.ctype,self.grantType,self.clientID,self.clientSecret,self.redirectUri)
		NewsOAuth2ConsumerService.bindGadget(self.widgetID,self.serviceName,self.clientName,"false")
		result = NewsOAuth2ConsumerService.unbindGadget(self.widgetID,self.serviceName)
		self.assert_(result != 0)
		NewsOAuth2ConsumerService.deleteClient(self.clientName)
		NewsOAuth2ConsumerService.deleteProvider(self.providerName)
		
	def test_unbindNonExistentGadget(self):
		result = NewsOAuth2ConsumerService.unbindGadget("Doesn't","exist")
		self.assertEqual(result,None)
		
	def test_purgeAllTokens(self):
		flag = 0
		
		try:
			NewsOAuth2ConsumerService.purgeAllTokens()
		except:
			flag = 1
		
		self.assertEqual(flag,0)
		
def suite():
    suite = unittest.TestSuite()
    suite.addTest(unittest.makeSuite(NewsOAuth2ConsumerServiceTest))
    return suite
	
if __name__ == '__main__':
	suite = unittest.TestLoader().loadTestsFromTestCase(NewsOAuth2ConsumerServiceTest)
    result = unittest.TextTestRunner(verbosity=2).run(suite)
    if len(result.errors) > 0 or len(result.failures) > 0:
        sys.exit(1)
