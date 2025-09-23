#!/bin/bash

FILE_1="$1"
FILE_2="$2"
FILE_OUT="${WORKSPACE}/jacoco_report.deltas"
PASSING_COVERAGE_THRESHOLD=80

rm -fv "${FILE_OUT}"

if [ ! -f "${FILE_1}" ]; then
	echo "${FILE_1} does not exist"
	exit 0
fi

if [ ! -f "${FILE_2}" ]; then
	echo "${FILE_2} does not exist"
	exit 0
fi

COVERED_LINES_1=`grep -i "covered lines" "${FILE_1}" | awk -F: '{print $2}'  | tr -d ' '`
COVERED_LINES_2=`grep -i "covered lines" "${FILE_2}" | awk -F: '{print $2}'  | tr -d ' '`
COVERED_LINES_DELTA=`echo "scale=2; ${COVERED_LINES_2} - ${COVERED_LINES_1}" | bc`

MISSED_LINES_1=`grep -i "missed lines" "${FILE_1}" | awk -F: '{print $2}'  | tr -d ' '`
MISSED_LINES_2=`grep -i "missed lines" "${FILE_2}" | awk -F: '{print $2}'  | tr -d ' '`
MISSED_LINES_DELTA=`echo "scale=2; ${MISSED_LINES_2} - ${MISSED_LINES_1}" | bc`

LINE_COVERAGE_1=`grep -i "line coverage" "${FILE_1}" | awk -F: '{print $2}'  | tr -d ' '`
LINE_COVERAGE_1=`echo "scale=2; 100 * ${LINE_COVERAGE_1}" | bc`

LINE_COVERAGE_2=`grep -i "line coverage" "${FILE_2}" | awk -F: '{print $2}'  | tr -d ' '`
LINE_COVERAGE_2=`echo "scale=2; 100 * ${LINE_COVERAGE_2}" | bc`

LINE_COVERAGE_DELTA=`echo "scale=2; ${LINE_COVERAGE_2} - ${LINE_COVERAGE_1}" | bc`

TOTAL_LINES_1=`grep -i "total lines" "${FILE_1}" | awk -F: '{print $2}'  | tr -d ' '`
TOTAL_LINES_2=`grep -i "total lines" "${FILE_2}" | awk -F: '{print $2}'  | tr -d ' '`
TOTAL_LINES_DELTA=`echo "scale=2; ${TOTAL_LINES_2} - ${TOTAL_LINES_1}" | bc`

TITLE_1=`grep -i "build label" "${FILE_1}" | awk -F: '{print $2}'  | tr -d ' ' | awk -F_ '{print $3}'`
TITLE_2=`grep -i "build label" "${FILE_2}" | awk -F: '{print $2}'  | tr -d ' ' | awk -F_ '{print $3}'`
TITLE_3=Delta

printf "\t\t%15s\t%15s\t%10s\n" "${TITLE_1}" "${TITLE_2}" "${TITLE_3}"
printf "Line Coverage\t%14.2f%%\t%14.2f%%\t%9.2f%%\n" ${LINE_COVERAGE_1} ${LINE_COVERAGE_2} ${LINE_COVERAGE_DELTA}
printf "Covered Lines\t%15d\t%15d\t%10d\n" ${COVERED_LINES_1} ${COVERED_LINES_2} ${COVERED_LINES_DELTA}
printf "Missed Lines\t%15d\t%15d\t%10d\n" ${MISSED_LINES_1} ${MISSED_LINES_2} ${MISSED_LINES_DELTA}
printf "Total Lines\t%15d\t%15d\t%10d\n" ${TOTAL_LINES_1} ${TOTAL_LINES_2} ${TOTAL_LINES_DELTA}

echo "h2. Code Coverage Deltas" > "${FILE_OUT}"
echo "||||${TITLE_1}||${TITLE_2}||${TITLE_3}||" >> "${FILE_OUT}"
echo "|Line Coverage|${LINE_COVERAGE_1}%|${LINE_COVERAGE_2}%|${LINE_COVERAGE_DELTA}%|" >> "${FILE_OUT}"
echo "|Covered Lines|${COVERED_LINES_1}|${COVERED_LINES_2}|${COVERED_LINES_DELTA}|" >> "${FILE_OUT}"
echo "|Missed Lines|${MISSED_LINES_1}|${MISSED_LINES_2}|${MISSED_LINES_DELTA}|" >> "${FILE_OUT}"
echo "|Total Lines|${TOTAL_LINES_1}|${TOTAL_LINES_2}|${TOTAL_LINES_DELTA}|" >> "${FILE_OUT}"

echo "" | tee -a "${FILE_OUT}"

if [ ${TOTAL_LINES_DELTA} -le 0 ]; then
	echo "Total lines did not increase." | tee -a "${FILE_OUT}"
	exit 0
fi

LINE_COVERAGE_NEW=`echo "scale=2; 100 * ${COVERED_LINES_DELTA}/${TOTAL_LINES_DELTA}" | bc`
echo "Line Coverage for New Code: ${LINE_COVERAGE_NEW}%" | tee -a "${FILE_OUT}"

RESULT=`echo "${LINE_COVERAGE_NEW} < ${PASSING_COVERAGE_THRESHOLD}" | bc -l`
if [ ${RESULT} == 1 ]; then
	echo ""
	echo "Line coverage for new code is less than ${PASSING_COVERAGE_THRESHOLD}%." | tee -a "${FILE_OUT}"
	exit 10
fi

exit 0
