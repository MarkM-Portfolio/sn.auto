#!/bin/bash

export test_result_xml_dir=$1

echo "Building JUnit report xml file..."
bld -f ${CI_COMMON_HOME}/build_junit_report.xml ci.build.junitreport
