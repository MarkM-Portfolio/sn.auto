#!/bin/sh

if [ "$#" -ne 3 ]; then
	echo "Usage: $0 <Full path to \"exclude\" file> <Full path to input file> <Full path to output file>"
	exit 1
fi

excludeListFile=$1
inputFile=$2
outputFile=$3

if [ ! -f "${excludeListFile}" ];then
	echo "Could not find \"exclude\" file: ${excludeListFile}"
	exit 1
fi

if [ ! -f "${inputFile}" ];then
	echo "Could not find input file: ${inputFile}"
	exit 1
fi

# Save a copy of input file just in case.
cp ${inputFile} ${TMP}/${inputFile}_sav

> ${outputFile}
if [ $? != 0 ]; then
	echo "Could not write to output file: ${outputFile}"
	exit 1
fi

# Create an array of test exclusions from the test exclusion text file.
i=1
while read testExclude
do
    echo ${testExclude} | grep "^#" > /dev/null 2>&1
    if [ $? == 0 ]; then
        # Got a comment line.
        continue
    fi

	echo "Test to exclude: ${testExclude}"
	classNamesToExclude[$i]=`echo ${testExclude} | awk -F '"' '{print $2}'`
	testNamesToExclude[$i]=`echo ${testExclude} | awk -F '"' '{print $4}'`
	i=`expr $i + 1`
done < ${excludeListFile}

numExcludedTests=${#testNamesToExclude[@]}
echo "Number tests to exclude: ${numExcludedTests}"

numTests=0
numFailures=0
numErrors=0

numExclusions=0
numFailureExclusions=0
numErrorExclusions=0

while read inputLine
do
	foundTestFailureToExclude=false
	foundTestErrorToExclude=false
	
	echo "${inputLine}" | grep '<testcase' > /dev/null 2>&1
	if [ $? != 0 ]; then
		echo "${inputLine}" >> ${outputFile}
		continue
	fi

	# Got a <testcase line. See if there is a closing </testcase on same line.
	numTests=`expr ${numTests} + 1`	
	echo "${inputLine}" | grep '</testcase' > /dev/null 2>&1
	if [ $? == 0 ]; then
		# Got a passing test.
		echo "${inputLine}" >> ${outputFile}
		continue
	fi

	failureLine="${inputLine}"
	# Read the next line to see if it is a failure or error.
	read inputLine
		
	echo "${inputLine}" | grep '<failure' > /dev/null 2>&1
	if [ $? == 0 ]; then
		echo "Got failure."
		foundTestFailureToExclude=true
		numFailures=`expr ${numFailures} + 1`
	fi

	echo "${inputLine}" | grep '<error' > /dev/null 2>&1
	if [ $? == 0 ]; then
		echo "Got error."
		foundTestErrorToExclude=true
		numErrors=`expr ${numErrors} + 1`
	fi

	# Got a failure or error. See if it's one that needs to be excluded.
	testName=`echo "${failureLine}" | awk -F'"' '{print $4}'`
	className=`echo "${failureLine}" | awk -F'"' '{print $2}'`
	grep "classname=\"${className}\" name=\"${testName}\"" ${excludeListFile} > /dev/null 2>&1
	if [ $? != 0 ]; then
		# Test not found in exclude file, so write out the line.
		foundTestFailureToExclude=false
		foundTestErrorToExclude=false
		echo "${failureLine}" >> ${outputFile}
		echo "${inputLine}" >> ${outputFile}
		continue
	fi

	# This is an excluded test.
	echo "Found test to exclude: classname=\"${className}\" name=\"${testName}\""
	numExclusions=`expr ${numExclusions} + 1`
	if [ ${foundTestFailureToExclude} == "true" ]; then
		numFailureExclusions=`expr ${numFailureExclusions} + 1`
	elif [ ${foundTestErrorToExclude} == "true" ]; then
		numErrorExclusions=`expr ${numErrorExclusions} + 1`
	fi
				
	# Skip until </testcase>.
	echo "Skipping to end of testcase..."
	while read inputLine
	do
		echo "${inputLine}" | grep '</testcase' > /dev/null 2>&1
		if [ $? != 0 ]; then
			# Did not find end of testcase.
			continue
		fi
		
		# Found the end of the testcase.
		echo "Found end of testcase"
		break
	done
done < ${inputFile}

echo "numExclusions: ${numExclusions}"
echo "numFailureExclusions: ${numFailureExclusions}"
echo "numErrorExclusions: ${numErrorExclusions}"

# Need to modify the test totals in the xml file; the line to modify will look like:
# <testsuite errors="1" failures="2" hostname="connemmawas" name="com.ibm.lconn.share.services.test.suite.WikisTestsSuite" tests="254" time="151.563" timestamp="2013-04-05T18:26:31">
# It will be easier to count up the errors and failures from the file
# rather than trying to parse the above line in a shell script.
echo "numTests=${numTests}"
echo "numFailures=${numFailures}"
echo "numErrors=${numErrors}"

modifiedNumTests=`expr ${numTests} - ${numExclusions}`
modifiedNumFailures=`expr ${numFailures} - ${numFailureExclusions}`
modifiedNumErrors=`expr ${numErrors} - ${numErrorExclusions}`

echo "modifiedNumTests: ${modifiedNumTests}"
echo "modifiedNumFailures: ${modifiedNumFailures}"
echo "modifiedNumErrors: ${modifiedNumErrors}"

sed -i -e "s/tests=\"[0-9]*\"/tests=\"${modifiedNumTests}\"/" ${outputFile}
sed -i -e "s/errors=\"[0-9]*\"/errors=\"${modifiedNumErrors}\"/" ${outputFile}
sed -i -e "s/failures=\"[0-9]*\"/failures=\"${modifiedNumFailures}\"/" ${outputFile}

# Copy output file back to input file.
cp ${outputFile} ${inputFile}

exit 0
