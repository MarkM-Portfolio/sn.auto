import unittest
import sys, os
import re
import java
import string
import random

lc_admin_dir = os.environ.get("IC_ADMIN_DIR", "bin_lc_admin")
if not lc_admin_dir in sys.path:
    sys.path.append(lc_admin_dir)
if not globals().has_key('CommunitiesConfigService'):
    execfile(os.path.join(lc_admin_dir, 'communitiesAdmin.py'))

def id_generator(size=8, chars=string.uppercase + string.digits):
    choices = []
    for x in range(size):
        choices.append(random.choice(chars))
    return ''.join(choices)

class CommunitiesAdminTest(unittest.TestCase):
    def setUp(self):
        self.users = [
        {'name': 'ajones101', 'mail': 'ajones101@janet.iris.com' },
        {'name': 'ajones102', 'mail': 'ajones102@janet.iris.com' }
        ]

    def test_check_out_cfg_files(self):
        cellName = AdminControl.getCell()
        CommunitiesConfigService.checkOutConfig("tmp", cellName)
        self.assert_(os.path.exists(os.path.join("tmp", "communities-config.xml")))
        self.assert_(os.path.exists(os.path.join("tmp", "communities-config.xsd")))
        CommunitiesConfigService.showConfig()
        CommunitiesConfigService.checkInConfig()


    def test_check_out_policy_files(self):
        cellName = AdminControl.getCell()
        CommunitiesConfigService.checkOutPolicyConfig("tmp", cellName)
        self.assert_(os.path.exists(os.path.join("tmp", "communities-policy.xml")))
        self.assert_(os.path.exists(os.path.join("tmp", "communities-policy.xsd")))
        CommunitiesConfigService.checkInPolicyConfig("tmp", cellName)

    def test_sync_membership(self):
        CommunitiesMemberService.syncAllMembersByExtId({"updateOnEmailLoginMatch": "true"})
        CommunitiesMemberService.syncAllMembersByExtId({"updateOnEmailLoginMatch": "false"})

    def test_inactivate_user(self):
        #CommunitiesService.inactivateUser(self.users[0]['mail'])
        pass

    def test_create_community_by_mail(self):
        name = "wsadmin-test " + id_generator()
        CommunitiesService.createCommunityWithEmail(name, self.users[0]['mail'], 1, "empty.xml")

    def test_create_community_by_login(self):
        name = "wsadmin-test " + id_generator()
        CommunitiesService.createCommunityWithLoginName(name, self.users[0]['name'], 1, "empty.xml")

def suite():
    suite = unittest.TestSuite()
    suite.addTest(unittest.makeSuite(CommunitiesAdminTest))
    return suite

if __name__ == '__main__':
    # unitest.main() is not supported by wsadmin.sh
    #unittest.main()
    # the Jython implementation from WAS doesn't follow the normal Python/Jython
    # behavior, the sys.argv doesn't contain the name of the script itself, so
    # we need to workaround that to find the script name.

    suite = unittest.TestLoader().loadTestsFromTestCase(CommunitiesAdminTest)
    result = unittest.TextTestRunner(verbosity=2).run(suite)
    if len(result.errors) > 0 or len(result.failures) > 0:
        sys.exit(1)
