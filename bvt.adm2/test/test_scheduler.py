import unittest
import sys, os
import re
import java

#
# get Connections wsadmin sripts into the system path
#
lc_admin_dir = os.environ.get("IC_ADMIN_DIR", "bin_lc_admin")
if not lc_admin_dir in sys.path:
    sys.path.append(lc_admin_dir)
if not globals().has_key('Scheduler'):
  execfile(os.path.join(lc_admin_dir, 'connectionsConfig.py'))


class SchedulerAdminTest(unittest.TestCase):
    def setUp(self):
        pass

    def test_listAllTasks(self):
        Scheduler.listAllTasks()

    # WARNING: this requres the WAS server to be restarted in order
    #          to regenerate the tasks again.
    def xtest_clearAllTasks(self):
        Scheduler.clearAllTasks()
        
def suite():
    suite = unittest.TestSuite()
    suite.addTest(unittest.makeSuite(SchedulerAdminTest))
    return suite

if __name__ == '__main__':
    # unitest.main() is not supported by wsadmin.sh
    #unittest.main()
    # the Jython implementation from WAS doesn't follow the normal Python/Jython
    # behavior, the sys.argv doesn't contain the name of the script itself, so
    # we need to workaround that to find the script name.

    suite = unittest.TestLoader().loadTestsFromTestCase(SchedulerAdminTest)
    result = unittest.TextTestRunner(verbosity=2).run(suite)
    if len(result.errors) > 0 or len(result.failures) > 0:
        sys.exit(1)
