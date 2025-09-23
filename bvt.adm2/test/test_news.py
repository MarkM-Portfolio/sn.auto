import unittest
import sys, os
import re
import java

lc_admin_dir = os.environ.get("IC_ADMIN_DIR", "bin_lc_admin")
if not lc_admin_dir in sys.path:
    sys.path.append(lc_admin_dir)
if not globals().has_key('NewsActivityStreamService'):
    execfile(os.path.join(lc_admin_dir, 'newsAdmin.py'))

class NewsAdminTest(unittest.TestCase):
    def setUp(self):
        pass

    def test_check_out_cfg_files(self):
        cellName = AdminControl.getCell()
        NewsCellConfig.checkOutConfig("tmp", cellName)
        self.assert_(os.path.exists(os.path.join("tmp", "news-config.xml")))
        self.assert_(os.path.exists(os.path.join("tmp", "news-config.xsd")))
        NewsCellConfig.checkInConfig()

    def test_NewsEmailDigestService(self):
        self.assert_(0 < NewsEmailDigestService.loadBalanceEmailDigest())

    def test_news_scheduler(self):
        NewsScheduler.getTaskDetails("NewsDataCleanup")

    def test_as_listApplicationRegistrations(self):
        self.assert_(NewsActivityStreamService.listApplicationRegistrations())

    # Sameple returns from getApplicationRegistration('communities') looks like below
    # {isDigestEnabled=false,
    #  imageUrl={webresources}/web/com.ibm.lconn.core.styles/images/iconCommunities16.png,
    #  summary=null, isEnabled=true, secureUrl={communities}, isDigestLocked=false,
    #  defaultFollowFrequency=null,
    #  secureImageUrl={webresources}/web/com.ibm.lconn.core.styles/images/iconCommunities16.png,
    #  appId=communities, displayName=communities, url={communities}}
    def test_as_getApplicationRegistration(self):
        app_reg=NewsActivityStreamService.getApplicationRegistration("communities")
        self.assertEqual('communities', app_reg["appId"])
        self.assertEqual('communities', app_reg["displayName"])
        self.assertEqual('true', app_reg["isEnabled"])

    # test register a new activity stream service and then update, remove it
    def test_as_service_registration(self):
        self.assertEqual('testApp', NewsActivityStreamService.registerApplication("testApp", "Test Application",
            "http://www.test.com/gadget.xml", "https://www.test.com/gadget.xml",
            "http://www.test.com/image.jpg", "https://www.test.com/image.jpg", "summary", "true"))
        self.assert_(NewsActivityStreamService.updateApplicationRegistration("testApp", "isEnabled", "false"))
        self.assert_(NewsActivityStreamService.removeApplicationRegistration("testApp"))

    def test_clearWidgetCaches(self):
        NewsWidgetCatalogService.clearWidgetCaches()

    def test_countWidgets(self):
        n_all = NewsWidgetCatalogService.countWidgets(Enablement.ALL)
        self.assert_( 27 <= n_all )
        ne = NewsWidgetCatalogService.countWidgets(Enablement.ENABLED)
        nd = NewsWidgetCatalogService.countWidgets(Enablement.DISABLED)
        self.assert_( n_all == ne + nd )
        n = NewsWidgetCatalogService.countWidgets()
        self.assert_( n == n_all )

    def test_browseWidgets(self):
        # assume we don't have too many widgets defiend
        widgets = NewsWidgetCatalogService.browseWidgets(Enablement.ALL)
        w_count = NewsWidgetCatalogService.countWidgets(Enablement.ALL)
        self.assertEqual(len(widgets), w_count)
        widgets = NewsWidgetCatalogService.browseWidgets(Enablement.ALL, 2, 1)
        self.assertEqual(len(widgets), 2)

    def test_findWidgetById(self):
        widget = NewsWidgetCatalogService.browseWidgets(Enablement.ALL, 3, 1)[2]
        w = NewsWidgetCatalogService.findWidgetById(widget['widgetId'])
        for attr in ['widgetId', 'isSystem', 'title', 'url']:
            self.assertEqual(w[attr], widget[attr])

    def test_findWidgetByUrl(self):
        widget = NewsWidgetCatalogService.browseWidgets(Enablement.ALL, 3, 1)[2]
        w = NewsWidgetCatalogService.findWidgetByUrl(widget['url'])
        for attr in ['widgetId', 'isSystem', 'title', 'url']:
            self.assertEqual(w[attr], widget[attr])

        
def suite():
    suite = unittest.TestSuite()
    suite.addTest(unittest.makeSuite(NewsAdminTest))
    return suite

if __name__ == '__main__':
    # unitest.main() is not supported by wsadmin.sh
    #unittest.main()
    # the Jython implementation from WAS doesn't follow the normal Python/Jython
    # behavior, the sys.argv doesn't contain the name of the script itself, so
    # we need to workaround that to find the script name.
    suite = unittest.TestLoader().loadTestsFromTestCase(NewsAdminTest)
    result = unittest.TextTestRunner(verbosity=2).run(suite)
    if len(result.errors) > 0 or len(result.failures) > 0:
        sys.exit(1)


