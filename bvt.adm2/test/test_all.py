import unittest

#import test_activities
import test_news
import test_profiles
import test_scheduler
import test_NewsOAuth
import test_OAuthApplication
import test_communities
import test_communities_reparent

suites = []
#suites.append(test_activities.suite())
suites.append(test_news.suite())
suites.append(test_profiles.suite())
suites.append(test_scheduler.suite())
suites.append(test_OAuthApplication.suite())
suites.append(test_NewsOAuth.suite())
suites.append(test_communities.suite())
suites.append(test_communities_reparent.suite())

suite = unittest.TestSuite()
for s in suites:
    suite.addTest(s)

result = unittest.TextTestRunner(verbosity=2).run(suite)

if len(result.errors) > 0 or len(result.failures) > 0:
    sys.exit(1)
