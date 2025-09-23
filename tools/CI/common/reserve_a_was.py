import time
import os
import sys
import json as jparser
from dashboard_api import Dashboard_API

program_path = sys.argv[0]
server_pool = sys.argv[1]
ret_val = 0

try:	
	t = Dashboard_API(base_url = "https://icautomation.cnx.cwp.pnp-hcl.com")
	
	# Authenticate.
	pwd_file = open("/local/ci/common/.icci", "r")
	pwd = pwd_file.read().strip()
	pwd_file.close()
	a = t.auth("icci@us.ibm.com", pwd)
	if a == 1:
		raise Exception, "Dashboard API Authentication error: %s" %(t.error_message)

	# Request a reservation.
	#r = t.reserve_server(os_password = "lcsecret", was_password="lcsecret")
	r = t.reserve_server(pool = server_pool)
	jobj = jparser.loads(r)
	
	error = jobj['error']
	if error != "no error":
		raise Exception, "Failed to reserve a server from pool: %s, error: %s" %(server_pool, error)

	ticket_id = jobj['ticket']['id']

	# Poll until server is reserved.
	cnt = 0
	while True:
		cnt = cnt + 1
		if cnt > 300:
			t.cancel_ticket(ticket_id)
			raise Exception, "Could not reserve a server from pool: %s in 10 minutes; giving up..." %(server_pool)
	
		time.sleep(2)
	
		r = t.get_ticket(ticket_id)
		jobj = jparser.loads(r)
		error = jobj['error']
		if error != "no error":
			continue
	
		state = jobj['ticket']['state']
		if state == 1:
			break
	
	server_name = jobj['ticket']['server_name']
	server_id = jobj['ticket']['server_id']
	password = jobj['ticket']['password']
	
	print "%s,%s,%s" %(server_name,server_id,password)

except Exception, errMsg:
	print "Exception received in %s: %s\n" %(program_path, str(errMsg))
	ret_val = 1

finally:
	t.cleanUp()

sys.exit(ret_val)
