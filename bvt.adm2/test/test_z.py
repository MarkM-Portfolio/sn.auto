import unittest
import sys, os
import re
#import java
import xmlrunner

class ZTest(unittest.TestCase):
    def setUp(self):
        self.users = [
        {'name': 'Amy Jones100', 'mail': 'ajones100@janet.iris.com' }
        ]

    def test_should_always_pass(self):
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

    suite = unittest.TestLoader().loadTestsFromTestCase(ZTest)
    xmlrunner.XMLTestRunner().run(suite)


