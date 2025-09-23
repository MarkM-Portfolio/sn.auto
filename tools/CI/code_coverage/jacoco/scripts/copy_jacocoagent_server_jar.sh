#!/bin/sh

for i in `seq 1 100`
do
	echo "Copying jacocoagent_server.jar to lcauto${i}.swg.usma.ibm.com:/opt/IBM/WebSphere/AppServer/java/jre/lib/ext/jacocoagent.jar..."
	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no jacocoagent_server.jar lcuser@lcauto${i}.swg.usma.ibm.com:/opt/IBM/WebSphere/AppServer/java/jre/lib/ext/jacocoagent.jar
	#ssh lcuser@lcauto${i}.swg.usma.ibm.com ls /opt/IBM/WebSphere/AppServer/java/jre/lib/ext
	[ $? -eq 0 ] || echo "Copy failed."
done

exit 0
