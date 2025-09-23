#!/bin/sh

${CI_COMMON_HOME}/get-was-creds.sh
[ $? -eq 0 ] || echo "WARNING: Could not get WAS credentials."

${CI_COMMON_HOME}/get-was-logs.sh
[ $? -eq 0 ] || echo "WARNING: Could not get WAS logs."

${CI_COMMON_HOME}/get-was-status.sh
[ $? -eq 0 ] || echo "WARNING: Could not get WAS status."

exit 0
