#!/bin/bash
/usr/bin/rsync -avzhe ssh /var/log/httpd/apps_collabservsvt2_access_log.1.gz root@scvitals.swg.usma.ibm.com:/logs/rp1a.bht6.swg.usma.ibm.com/
