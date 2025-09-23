#!/bin/bash
PROT=ftps://
HOST=ftp.collabservsvt2.swg.usma.ibm.com
USER=fvtorgadmin1@ivthouse.com
PASS=passw0rd
DATE=$(date --date="yesterday" +"%Y-%m-%d").

/bin/rm -f /logs/journals/*
/usr/bin/lftp -p 990 -u $USER,$PASS -e "mget journal/$DATE* -O /logs/journals/;bye" $PROT$HOST
/bin/gunzip /logs/journals/$DATE*
/usr/bin/rename $DATE '' /logs/journals/*.txt
/usr/bin/perl /root/scvitals/scvitals/scvitals/monitor.pl $HOST
