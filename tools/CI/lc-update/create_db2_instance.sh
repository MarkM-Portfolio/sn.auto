#! /bin/bash

DB2INST_NUM=$1
if [ -z "${DB2INST_NUM}" ]; then
	echo "You must input a DB2 instance number."
	exit 1
fi

DB2INST=db2inst${DB2INST_NUM}

HOSTNAME=`hostname`
if [ "${HOSTNAME}" == "jenksub1" -o "${HOSTNAME}" == "jenksub2" ]; then
	DB2_HOME=/opt/ibm/db2/V10.1
	DB2INST_HOME=/home
	DB2INST_PORT_BASE=49999
elif [ "${HOSTNAME}" == "connectionsci1" ]; then
	DB2_HOME=/local/opt/ibm/db2/V9.7
	DB2INST_HOME=/local/home/db2s
	DB2INST_PORT_BASE=50000
fi
	
DB2INST_PORT=`expr ${DB2INST_PORT_BASE} + ${DB2INST_NUM}`

# Add user to OS.
echo "Adding user ${DB2INST} to OS..."
sudo su - -c "useradd  -d ${DB2INST_HOME}/${DB2INST} -e 9999-12-31 -G dasadm1 -m -s /bin/bash ${DB2INST}"
if [ $? != 0 ]; then
	echo "Error adding user ${DB2INST} to OS."
	exit 1
fi

# Fix up password stuff
sudo su - -c "chage -E -1 -m 0 -M 99999 ${DB2INST}"
if [ $? != 0 ]; then
	echo "Error configing password."
	exit 1
fi

# Create the DB2 instance.
echo "Creating DB2 instance: ${DB2INST}..."
sudo su - -c "${DB2_HOME}/instance/db2icrt -u ${DB2INST} -p db2c_${DB2INST} ${DB2INST}"
if [ $? != 0 ]; then
	echo "Error creating ${DB2INST}."
	exit 1
fi

# Add to /etc/services.
echo "Adding \"db2c_${DB2INST}   ${DB2INST_PORT}/tcp\" to /etc/services..."
sudo su - -c "echo \"db2c_${DB2INST}   ${DB2INST_PORT}/tcp\" >> /etc/services"
if [ $? != 0 ]; then
	echo "Error adding to /etc/services."
	exit 1
fi

# Start the DB2 instance.
echo "Starting ${DB2INST}..."
sudo su - ${DB2INST} -c "db2start"
if [ $? != 0 ]; then
	echo "Error starting ${DB2INST}."
	exit 1
fi

exit 0
