#!/bin/ksh

BUILD_STREAM=$1
BUILD_COMPONENT=$2

$HOME/jython-2.5.2/jython -Dpython.path="${HOME}/emma/wink-json4j-1.1.3-incubating.jar" create_build_request_cd.py ${BUILD_STREAM} ${BUILD_COMPONENT}
if [[ $? -ne 0 ]]; then
    print "Failed to submit build requests."
    stty sane
    exit 1
fi

stty sane
exit 0
