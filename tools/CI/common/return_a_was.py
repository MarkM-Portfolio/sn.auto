import time
import os
import sys
import json as jparser
from dashboard_api import Dashboard_API

program_path = sys.argv[0]
server_id = sys.argv[1]
ret_val = 0
print "Entered %s; server_id: %s\n" %(program_path, server_id)

try:
	t = Dashboard_API(base_url = "https://icautomation.cnx.cwp.pnp-hcl.com")

	# Authenticate.
	pwd_file = open("/local/ci/common/.icci", "r")
	pwd = pwd_file.read().strip()
	pwd_file.close()
	a = t.auth("icci@us.ibm.com", pwd)
	if a == 1:
		raise Exception, "Dashboard API Authentication error: %s" %(t.error_message)

	# Return the server.
	print "Returning server with id: %s..." %(server_id)
	r = t.return_server(server_id)

	jobj = jparser.loads(r)
	error = jobj['error']
	if error != "no error":
		raise Exception, "Failed to return server with id: %s, error: %s" %(server_id, error)

except Exception, errMsg:
	print "Exception received in %s: %s\n" %(program_path, str(errMsg))
	ret_val = 1

finally:
	t.cleanUp()

sys.exit(ret_val)
