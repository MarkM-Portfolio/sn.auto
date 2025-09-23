import unittest
import sys, os
import re
import java

lc_admin_dir = os.environ.get("IC_ADMIN_DIR", "bin_lc_admin")
if not lc_admin_dir in sys.path:
    sys.path.append(lc_admin_dir)
if not globals().has_key('ProfilesConfigService'):
    execfile(os.path.join(lc_admin_dir, 'profilesAdmin.py'))

class ProfilesAdminTest(unittest.TestCase):
    def setUp(self):
        self.users = [
        {'name': 'Amy Jones97', 'mail': 'ajones97@janet.iris.com' }
        ]

    def test_check_out_cfg_files(self):
        cellName = AdminControl.getCell()
        ProfilesConfigService.checkOutConfig("tmp", cellName)
        self.assert_(os.path.exists(os.path.join("tmp", "profiles-config.xml")))
        self.assert_(os.path.exists(os.path.join("tmp", "profiles-config.xsd")))
        ProfilesConfigService.showConfig()
        ProfilesConfigService.checkInConfig()


    def test_check_out_policy_files(self):
        cellName = AdminControl.getCell()
        ProfilesConfigService.checkOutPolicyConfig("tmp", cellName)
        self.assert_(os.path.exists(os.path.join("tmp", "profiles-policy.xml")))
        self.assert_(os.path.exists(os.path.join("tmp", "profiles-policy.xsd")))
        ProfilesConfigService.checkInPolicyConfig("tmp", cellName)

    def test_purge_event_logs(self):
        # this one is disabled in product due to performance concerns
        #self.assert_(ProfilesService.purgeEventLogs())
        ProfilesService.purgeEventLogsByDates("06/21/2009", "06/26/2009")

    def test_delete_photo(self):
        rc = ProfilesService.deletePhoto(self.users[0]['mail'])
        self.assert_(rc is None, "return code expect to be None")

    def test_update_description(self):
        rc = ProfilesService.updateDescription(self.users[0]['mail'],
            "test description update for admin bvt tests")
        self.assert_(rc is None, "return code expect to be None")

    def test_update_experience(self):
        rc = ProfilesService.updateExperience(self.users[0]['mail'],
            "test experience update for admin bvt tests")
        self.assert_(rc is None, "return code expect to be None")

    def test_inactivate_user(self):
        #ProfilesService.inactivateUser(self.users[0]['mail'])
        pass

def suite():
    suite = unittest.TestSuite()
    suite.addTest(unittest.makeSuite(ProfilesAdminTest))
    return suite

if __name__ == '__main__':
    # unitest.main() is not supported by wsadmin.sh
    #unittest.main()
    # the Jython implementation from WAS doesn't follow the normal Python/Jython
    # behavior, the sys.argv doesn't contain the name of the script itself, so
    # we need to workaround that to find the script name.

    suite = unittest.TestLoader().loadTestsFromTestCase(ProfilesAdminTest)
    result = unittest.TextTestRunner(verbosity=2).run(suite)
    if len(result.errors) > 0 or len(result.failures) > 0:
        sys.exit(1)
