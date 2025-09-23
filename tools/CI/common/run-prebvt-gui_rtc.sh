#!/bin/sh

export NUM_TESTS_RUN=0
export NUM_TESTS_RUN_ORIG=0
export NUM_TESTS_RUN_RERUN=0
export NUM_FAILURES=0
export NUM_SKIPS=0
export PREBVT_RAN=true
OK=false

TEST_TEMPLATE_FILE_NAME=testTemplate_PREBVT_CI.xml
CI_PREBVT_GRID_SERVER_HOST=lcrft04.cnx.cwp.pnp-hcl.com
CI_PREBVT_GRID_SERVER_PORT=5555
CI_PREBVT_BROWSER_URL=http://${WAS_HOST_FQDN}:9080/

get_results(){
	# Check the log for failures.
	# End of log will have a line like:
	# Total tests run: 2, Failures: 0, Skips: 0
	NUM_TESTS_RUN=`grep 'Total tests run:' run-bvt.log | awk -F, '{print $1}' | awk -F: '{printf "%d\n", $2}'`
	NUM_FAILURES=`grep 'Total tests run:' run-bvt.log | awk -F, '{print $2}' | awk -F: '{printf "%d\n", $2}'`
	NUM_SKIPS=`grep 'Total tests run:' run-bvt.log | awk -F, '{print $3}' | awk -F: '{printf "%d\n", $2}'`
	echo "NUM_TESTS_RUN: ${NUM_TESTS_RUN}"
	echo "NUM_FAILURES: ${NUM_FAILURES}"
	echo "NUM_SKIPS: ${NUM_SKIPS}"
}

finally(){
	${CI_COMMON_HOME}/send_prebvt_mail.sh gui ${OK}
}

trap 'finally' EXIT

if [ -n "${errors_prebvt}" -a "${errors_prebvt}" != 0 ]; then
	echo "There were errors in setting up for the pre-bvt (GUI) tests, so not running them."
	PREBVT_RAN=false
	exit 0
fi

# Copy the test template file from the ci common directory.
cd ${CI_HOME}/pre-bvt/bvt.dist
echo "Copying ${CI_COMMON_HOME}/${TEST_TEMPLATE_FILE_NAME} to `pwd`..."
cp ${CI_COMMON_HOME}/${TEST_TEMPLATE_FILE_NAME} .

# Edit the pre-bvt template file for this component.
echo "Editing ${TEST_TEMPLATE_FILE_NAME}..."
sed -i -e "s]CI_PREBVT_GRID_SERVER_HOST]${CI_PREBVT_GRID_SERVER_HOST}]" ${TEST_TEMPLATE_FILE_NAME}
sed -i -e "s]CI_PREBVT_GRID_SERVER_PORT]${CI_PREBVT_GRID_SERVER_PORT}]" ${TEST_TEMPLATE_FILE_NAME}
sed -i -e "s]CI_PREBVT_BROWSER_URL]${CI_PREBVT_BROWSER_URL}]" ${TEST_TEMPLATE_FILE_NAME}
sed -i -e "s]CI_PREBVT_TEST_NAME]${CI_PREBVT_TEST_NAME}]" ${TEST_TEMPLATE_FILE_NAME}
sed -i -e "s]CI_PREBVT_TEST_CLASS]${CI_PREBVT_TEST_CLASS}]" ${TEST_TEMPLATE_FILE_NAME}

# Run the tests.
echo "Running pre-bvt (GUI) tests..."
${JAVA_HOME}/bin/java -jar bvt.gui.jar -IBMtemplate ${TEST_TEMPLATE_FILE_NAME} 2>&1 | tee run-bvt.log

# Check for failures.
get_results
NUM_TESTS_RUN_ORIG=${NUM_TESTS_RUN}

if [ "${NUM_FAILURES}" != 0 -o "${NUM_SKIPS}" != 0 ]; then
	# There were failures and/or skips, so try a re-run. Copy the "re-run" template file out of the test output directory.
	TEST_TEMPLATE_FILE_NAME=testng-failed.xml
	echo "There were CI pre-bvt (GUI) failures and/or skips, so copying test-output/${TEST_TEMPLATE_FILE_NAME} to `pwd`..."
	cp test-output/${TEST_TEMPLATE_FILE_NAME} .
	
	echo "Re-running failed/skipped pre-bvt (GUI) tests..."
	${JAVA_HOME}/bin/java -jar bvt.gui.jar -IBMtemplate ${TEST_TEMPLATE_FILE_NAME} 2>&1 | tee run-bvt.log
	
	# Check for failures.
	get_results
	NUM_TESTS_RUN_RERUN=${NUM_TESTS_RUN}
fi

if [ "${NUM_TESTS_RUN_ORIG}" != 0 -a "${NUM_FAILURES}" == 0 -a "${NUM_SKIPS}" == 0 ]; then
		OK=true
fi

# Copy the contents of the test-output directory to the HTTP server area.
HTTP_DEST_DIR=${CI_PREBVT_HTTP_DEST_DIR}/gui
RESULTS_DIR=${CI_PREBVT_RESULTS_DIR}/gui

if [ ! -d ${HTTP_DEST_DIR} ]; then
	echo "Creating directory ${HTTP_DEST_DIR}..."
	mkdir -p ${HTTP_DEST_DIR}
fi

echo "Copying contents of test-output directory to ${HTTP_DEST_DIR}..."
rsync -avL --delete ${CI_HOME}/pre-bvt/bvt.dist/test-output/ ${HTTP_DEST_DIR} 

# Copy the doc directory to the results area.
echo "Copying ${CI_HOME}/pre-bvt/bvt.dist/doc to ${HTTP_DEST_DIR}..."
rsync -avL --delete ${CI_HOME}/pre-bvt/bvt.dist/doc ${HTTP_DEST_DIR} 

# Edit screenshot links to point to the newly copied directory tree. 
OUTPUT_HTML_FILE=${HTTP_DEST_DIR}/html/output.html
echo "Editing ${OUTPUT_HTML_FILE}..."
sed -i -e "s]file:${CI_HOME}/pre-bvt/bvt.dist/test-output]http://${CI_HOST_FQDN}/${RESULTS_DIR}]g" ${OUTPUT_HTML_FILE}

# Edit doc links in the suite results html file to point correct directory
echo "Editing doc links in html files in ${HTTP_DEST_DIR}/html..."
sed -i -e "s]../../doc/]../doc/]g" ${HTTP_DEST_DIR}/html/*.html

# Need to replace the index.html file in the root results directory
# with one that will point to the stuff in the html subdirectory.
# First save off index.html
cd ${HTTP_DEST_DIR}
echo "Copying ${HTTP_DEST_DIR}/index.html to ${HTTP_DEST_DIR}/index_root.html..."
cp index.html index_root.html

# Copy the index.html file from the html subdirectory
# to the root directory.
echo "Copying ${HTTP_DEST_DIR}/html/index.html to ${HTTP_DEST_DIR}..."
cp html/index.html .

# Edit the newly copied index.html so the links will refer
# to the content in the html directory.
echo "Editing ${HTTP_DEST_DIR}/index.html..."
sed -i -e "s]suites.html]html/suites.html]g" index.html
sed -i -e "s]overview.html]html/overview.html]g" index.html

# Create link to results directory in case we need to change the structure
# of that directory
if [ ! -d ${HTTP_SERVER_HOME}/htdocs/pre-bvt-ci/${CI_PREBVT_TEST_NAME} ]; then
	echo "Creating link to results..."
	sudo ln -s ${HTTP_SERVER_HOME}/htdocs/pre-bvt-ci-results/${CI_PREBVT_TEST_NAME} ${HTTP_SERVER_HOME}/htdocs/pre-bvt-ci/${CI_PREBVT_TEST_NAME}
fi

exit 0
