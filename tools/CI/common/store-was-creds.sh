#!/bin/sh

WAS_CREDS_FILE="${WORKSPACE}/was-creds.properties"
WAS_HOST_PWD=$(ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "echo \${WAS_HOST_PWD}")

echo "Creating ${WAS_CREDS_FILE}..."
echo "Server: ${WAS_HOST_FQDN}" > "${WAS_CREDS_FILE}"
echo "OS user: ${REMOTE_USER}" >> "${WAS_CREDS_FILE}"
echo "OS password: ${WAS_HOST_PWD}" >> "${WAS_CREDS_FILE}"
echo "WAS admin user: ${WAS_ADMIN}" >> "${WAS_CREDS_FILE}"
echo "WAS admin password: ${WAS_HOST_PWD}" >> "${WAS_CREDS_FILE}"

exit 0
