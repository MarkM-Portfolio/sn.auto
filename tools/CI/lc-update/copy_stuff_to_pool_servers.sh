#!/bin/sh

for i in `seq 1 100`
do
	echo "Copying /tmp/hystrix-config.properties to lcauto${i}.swg.usma.ibm.com:/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/config/cells/lc45linux1Node01Cell/LotusConnections-config..."
	scp /tmp/hystrix-config.properties lcuser@lcauto${i}.swg.usma.ibm.com:/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/config/cells/lc45linux1Node01Cell/LotusConnections-config
done
