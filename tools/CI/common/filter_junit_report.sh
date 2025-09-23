#!/bin/bash

test_result_xml_file=$1

echo "Filtering ${test_result_xml_file}..."
cp ${test_result_xml_file} in.txt
count=0
while [ ${count} -lt 10 ]
do
    perl -p -e 's]</testsuite>.*\n]</testsuite>]' in.txt > out.txt
    diff out.txt in.txt > /dev/null 2>&1
    if [ $? == 0 ]; then
		echo "Finished filtering, took ${count} iteration(s)."
        break
    fi
    cp out.txt in.txt
    count=`expr ${count} + 1`
done
echo "" >> out.txt
cp out.txt ${test_result_xml_file}

if [ ${count} -eq 10 ]; then
	echo "Could not finish filtering after ${count} iteration(s)."
fi
