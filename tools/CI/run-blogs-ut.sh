#!/bin/sh
CONN_SERVER="http://connciwas.swg.usma.ibm.com:9080"
THIS_DIR=`pwd`
CURL_OUTPUT_FILENAME=testresult.txt
CURL_OUTPUT_FILEPATH=${THIS_DIR}/${CURL_OUTPUT_FILENAME}

ut_running() {
    curl -s ${CONN_SERVER}/blogs/__internal/unittest/run | grep "Error 417:"
}


echo "Start the Blog unit test on ${CONN_SERVER}"
curl ${CONN_SERVER}/blogs/__internal/unittest/run?start=true
echo ""

echo "Check and wait unit test to finish..."
count=0
while ut_running ; do
    sleep 30
	count=`expr ${count} + 1`
	if [ ${count} -gt 60 ]; then
		echo "Timed out after waiting half an hour for unit test to finish."
		break
	fi
done

curl ${CONN_SERVER}/blogs/__internal/unittest/run --output ${CURL_OUTPUT_FILENAME}
if [ $? != 0 ]; then
	echo "Failed to get URL: ${CONN_SERVER}/blogs/__internal/unittest/run"
	exit 1
fi

cat ${CURL_OUTPUT_FILEPATH}

# When all the tests pass, the following will be in the output:
#
#     OK (85 tests)
#
# When there are failures and/or errors:
#
#     FAILURES!!!
#     Tests run: 84,  Failures: 0,  Errors: 1
#

TEST_COUNT="?"
FAILURE_COUNT="?"
ERROR_COUNT="?"

grep 'FAILURES' ${CURL_OUTPUT_FILEPATH} > /dev/null 2>&1
if [ $? == 0 ]; then
	echo "There were failures."
	TEST_COUNT=`grep 'Tests run' ${CURL_OUTPUT_FILEPATH} | cut -d ':' -f 2 | cut -d ',' -f 1 |  sed s]' ']'']g`
	FAILURE_COUNT=`grep 'Tests run' ${CURL_OUTPUT_FILEPATH} | cut -d ':' -f 3 | cut -d ',' -f 1 |  sed s]' ']'']g`
	ERROR_COUNT=`grep 'Tests run' ${CURL_OUTPUT_FILEPATH} | cut -d ':' -f 4 | cut -d ',' -f 1 |  sed s]' ']'']g`
else
	grep 'OK' ${CURL_OUTPUT_FILEPATH} | grep 'tests' > /dev/null 2>&1
	if [ $? != 0 ]; then
		echo "Did not find \"OK\" line in ${CURL_OUTPUT_FILEPATH}."
	else
		TEST_COUNT=`grep 'OK' ${CURL_OUTPUT_FILEPATH} | grep 'tests' | cut -d '(' -f2 | cut -d ' ' -f1 | sed 's] ]]g'`
		FAILURE_COUNT=0
		ERROR_COUNT=0
	fi
fi

echo ""
echo "#################################################################"
echo "Summary Test Results:"
echo "  Test Count:    ${TEST_COUNT}"
echo "  Failure Count: ${FAILURE_COUNT}"
echo "  Error Count:   ${ERROR_COUNT}"
echo ""
echo "Detailed test results in: ${CURL_OUTPUT_FILEPATH}"
echo "#################################################################"
echo ""

if [ ${TEST_COUNT} == '?' -o ${FAILURE_COUNT} != '0' -o ${ERROR_COUNT} != '0' ]; then
	exit 1
fi

exit 0
