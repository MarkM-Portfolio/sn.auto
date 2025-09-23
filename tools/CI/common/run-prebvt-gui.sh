#!/bin/sh

# Create the workitem.properties file
${CI_COMMON_HOME}/gen_workitem_params.sh assign_to_automation

send_mail() {
	local MAIL_FILE=mail.txt
	echo "Sending email notification..."
	echo "To: CONN-Automation-GUI@notesdev.ibm.com,CONN-Automation-CI@notesdev.ibm.com" > ${MAIL_FILE}
	echo "Subject: TestNG Failure!" >> ${MAIL_FILE}
	echo "" >> ${MAIL_FILE}
	echo "TestNG did not produce a JUnit result file." >> ${MAIL_FILE}
	echo "" >> ${MAIL_FILE}
	echo "Build: ${BUILD_LABEL}" >> ${MAIL_FILE}
	echo "Job: ${BUILD_URL}" >> ${MAIL_FILE}

	cat ${MAIL_FILE} | /usr/sbin/sendmail -tv
}

echo "`date +"%F %T,%3N"` [CI_CD] INFO Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh
if [ $? != 0 ]; then
	echo "`date +"%F %T,%3N"` [CI_CD] ERROR Failed to load ${CI_COMMON_HOME}/ci_functions.sh."
	exit 1
fi

fail_with_error() {
    echo_error "$@"
	${CI_COMMON_HOME}/get-was-info.sh
    exit 1
}

[ -n "${WAS_HOST_FQDN}" ] || fail_with_error "\${WAS_HOST_FQDN} is null."

WAS_HOST=`echo ${WAS_HOST_FQDN} | cut -d '.' -f 1`

CI_PREBVT_TEST_NAME=$1
CI_PREBVT_TEST_CLASS=$2

echo_info "CI_PREBVT_TEST_NAME: ${CI_PREBVT_TEST_NAME}"

. ${CI_COMMON_HOME}/system.properties || fail_with_error "Failed to load ${CI_COMMON_HOME}/system.properties."

# Download and unzip the latest Automation fe.zip
[ -d sn.auto ] && ( echo_info "Deleting source tree: `pwd`/sn.auto..."; rm -rf sn.auto )
${LCUPDATE_DIR}/bin/get-build.sh -f -B ${BUILD_STREAM}_Automation || fail_with_error "Could not download or unzip the Automation fe.zip"

cd "${CI_BVT_DIST_DIR}"

# Copy the template file into place and edit for the GUI prebvt.
TEST_TEMPLATE_FILE_NAME=testTemplate_PREBVT_CI.xml
CI_PREBVT_GRID_SERVER_HOST=lcrft04.cnx.cwp.pnp-hcl.com
CI_PREBVT_GRID_SERVER_PORT=5555
CI_PREBVT_BROWSER_URL=http://${WAS_HOST_FQDN}/

echo_info "Copying ${CI_COMMON_HOME}/${TEST_TEMPLATE_FILE_NAME} to `pwd`..."
cp ${CI_COMMON_HOME}/${TEST_TEMPLATE_FILE_NAME} .

echo_info "Editing ${TEST_TEMPLATE_FILE_NAME}..."
sed -i -e "s]CI_PREBVT_GRID_SERVER_HOST]${CI_PREBVT_GRID_SERVER_HOST}]" ${TEST_TEMPLATE_FILE_NAME}
sed -i -e "s]CI_PREBVT_GRID_SERVER_PORT]${CI_PREBVT_GRID_SERVER_PORT}]" ${TEST_TEMPLATE_FILE_NAME}
sed -i -e "s]CI_PREBVT_BROWSER_URL]${CI_PREBVT_BROWSER_URL}]" ${TEST_TEMPLATE_FILE_NAME}

if [ "${CI_PREBVT_TEST_NAME}" == "Moderation" ]; then
	sed -i -e "s]\"fluent_wait_timeout\" value=\"45\"]\"fluent_wait_timeout\" value=\"90\"]" ${TEST_TEMPLATE_FILE_NAME}
fi

# Using the XML format in the strings in case we need to have multiple packages per component
# for regression testing. Normal looping wouldn't be viable since this string will be used
# in a "sed" command to edit the template XML file.
case ${BUILD_COMPONENT} in
	Activities)
		CI_PREBVT_MAX_RETRIES=${CI_PREBVT_MAX_RETRIES:-1}
		CI_PREBVT_TEST_PACKAGES="<package name=\"com.ibm.conn.auto.tests.activities\" />"
		;;

	Blogs)
		CI_PREBVT_MAX_RETRIES=${CI_PREBVT_MAX_RETRIES:-0}
		CI_PREBVT_TEST_PACKAGES="<package name=\"com.ibm.conn.auto.tests.blogs\" />"
		;;

	Bookmarks)
		CI_PREBVT_MAX_RETRIES=${CI_PREBVT_MAX_RETRIES:-0}
		CI_PREBVT_TEST_PACKAGES="<package name=\"com.ibm.conn.auto.tests.dogear\" />"
		;;

	Communities)
		CI_PREBVT_MAX_RETRIES=${CI_PREBVT_MAX_RETRIES:-0}
		CI_PREBVT_TEST_PACKAGES="<package name=\"com.ibm.conn.auto.tests.communities\" />"
		;;

	Forums)
		CI_PREBVT_TEST_PACKAGES="<package name=\"com.ibm.conn.auto.tests.forums\" />"
		;;

	Homepage)
		CI_PREBVT_TEST_PACKAGES="<package name=\"com.ibm.conn.auto.tests.homepage\" />"
		;;

	Profiles)
		CI_PREBVT_TEST_PACKAGES="<package name=\"com.ibm.conn.auto.tests.profiles\" />"
		;;
	
	Search)
		CI_PREBVT_TEST_PACKAGES="<package name=\"com.ibm.conn.auto.tests.globalsearch\" />"
		;;

	Share)
		case ${CI_PREBVT_TEST_NAME} in
			Files)
				CI_PREBVT_TEST_PACKAGES="<package name=\"com.ibm.conn.auto.tests.files\" />"
				;;

			Wikis)
				CI_PREBVT_TEST_PACKAGES="<package name=\"com.ibm.conn.auto.tests.wikis\" />"
				;;

			Sharebox)
				CI_PREBVT_TEST_PACKAGES="<package name=\"com.ibm.conn.auto.tests.sharebox\" />"
				;;

			*)
				echo_warn "Unknown test name for Share."
				;;
		esac
		;;

	UI)
		CI_PREBVT_MAX_RETRIES=${CI_PREBVT_MAX_RETRIES:-0}
		;;

	WidgetsCal)
		CI_PREBVT_TEST_PACKAGES="<package name=\"com.ibm.conn.auto.tests.calendar\" />"
		;;
esac

CI_PREBVT_MAX_RETRIES=${CI_PREBVT_MAX_RETRIES:-1}
sed -i -e "s]CI_PREBVT_MAX_RETRIES]${CI_PREBVT_MAX_RETRIES}]" ${TEST_TEMPLATE_FILE_NAME}

case "${CI_PREBVT_TEST_CLASS}" in
	Level3)
		# This will run all on-prem test suites.
		sed -i -e "s]REPLACE_THIS_WITH_GROUPS_TO_RUN]\
		\n\
		<groups>																\n\
				<run>															\n\
				  <include name=\"smoke\" />									\n\
				  <include name=\"level2\" />									\n\
				  <include name=\"regression\" />								\n\
				</run>															\n\
		</groups>																\n\
		\n\
		]" ${TEST_TEMPLATE_FILE_NAME}
		
		sed -i -e "s]REPLACE_THIS_WITH_TESTS_TO_RUN]\
		\n\
		<test name=\"${CI_PREBVT_TEST_NAME}\" preserve-order=\"true\" enabled=\"true\">		\n\
			<packages>																		\n\
				  ${CI_PREBVT_TEST_PACKAGES}												\n\
			</packages>																		\n\
		</test>																				\n\
		\n\
		]" ${TEST_TEMPLATE_FILE_NAME}

		sed -i -e "s]parallel=\"tests\"]parallel=\"methods\"]" ${TEST_TEMPLATE_FILE_NAME}
		;;

	*)
		# Default is to run level2 BVT on-prem test suites.
		case "${CI_PREBVT_TEST_NAME}" in
			Infra)
				# This runs the special case for the Infra UI BVT GUI tests.
				sed -i -e "s]REPLACE_THIS_WITH_GROUPS_TO_RUN]\
				\n\
				<groups>														\n\
				  <run>															\n\
				    <include name=\"infra\" />									\n\
			      </run>														\n\
				</groups>														\n\
				\n\
				]" ${TEST_TEMPLATE_FILE_NAME}

				sed -i -e "s]REPLACE_THIS_WITH_TESTS_TO_RUN]\
				\n\
				<test name=\"infra\" preserve-order=\"true\" enabled=\"true\">	\n\
				  <packages>													\n\
				  <package name=\"com.ibm.conn.auto.tests.activities\" />		\n\
				  <package name=\"com.ibm.conn.auto.tests.blogs\" />			\n\
				  <package name=\"com.ibm.conn.auto.tests.calendar\" />			\n\
				  <package name=\"com.ibm.conn.auto.tests.communities\" />		\n\
				  <package name=\"com.ibm.conn.auto.tests.dogear\" />			\n\
				  <package name=\"com.ibm.conn.auto.tests.files\" />			\n\
				  <package name=\"com.ibm.conn.auto.tests.forums\" />			\n\
				  <package name=\"com.ibm.conn.auto.tests.homepage\" />			\n\
				  <package name=\"com.ibm.conn.auto.tests.profiles\" />			\n\
				  <package name=\"com.ibm.conn.auto.tests.sharebox\" />			\n\
				  <package name=\"com.ibm.conn.auto.tests.wikis\" />			\n\
				  </packages>													\n\
			    </test>															\n\
				\n\
				]" ${TEST_TEMPLATE_FILE_NAME}
				;;

			Files)
				# This runs the level2 BVT tests as well as the File Picker tests.
				sed -i -e "s]REPLACE_THIS_WITH_GROUPS_TO_RUN]\
				\n\
				<groups>														\n\
				  <run>															\n\
				    <include name=\"level2\" />									\n\
				    <include name=\"filepicker\" />								\n\
				  </run>														\n\
				</groups>														\n\
				\n\
				]" ${TEST_TEMPLATE_FILE_NAME}

				sed -i -e "s]REPLACE_THIS_WITH_TESTS_TO_RUN]\
				\n\
			    <test name=\"${CI_PREBVT_TEST_NAME}\" preserve-order=\"true\" enabled=\"true\">		\n\
			      <classes>																			\n\
			        <class name=\"com.ibm.conn.auto.tests.files.BVT_Level_2_Files\" />				\n\
			        <class name=\"com.ibm.conn.auto.tests.files.BVT_FilePicker\" />					\n\
			      </classes>																		\n\
			    </test>																				\n\
				\n\
				]" ${TEST_TEMPLATE_FILE_NAME}
				;;

		*)
		 		# The default runs the normal level2 BVT tests for a particular component.
				sed -i -e "s]REPLACE_THIS_WITH_GROUPS_TO_RUN]\
				\n\
				<groups>														\n\
				  <run>															\n\
				    <include name=\"level2\" />									\n\
				  </run>														\n\
				</groups>														\n\
				\n\
				]" ${TEST_TEMPLATE_FILE_NAME}

				sed -i -e "s]REPLACE_THIS_WITH_TESTS_TO_RUN]\
				\n\
			    <test name=\"${CI_PREBVT_TEST_NAME}\" preserve-order=\"true\" enabled=\"true\">		\n\
			      <classes>																			\n\
			        <class name=\"${CI_PREBVT_TEST_CLASS}\" />										\n\
			      </classes>																		\n\
			    </test>																				\n\
				\n\
				]" ${TEST_TEMPLATE_FILE_NAME}
				;;
		esac
esac

# Make sure the Grid is up and running.
is_grid_running || fail_with_error "Grid not running."

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh

# Run the tests.
OK=false
MAX_ATTEMPTS=2
NUM_ATTEMPTS=0

while [ ${NUM_ATTEMPTS} -lt ${MAX_ATTEMPTS} ]
do
	echo_info "Running pre-bvt (GUI) tests..."
	${JAVA_HOME}/bin/java -jar bvt.gui.jar -IBMtemplate ${TEST_TEMPLATE_FILE_NAME} 2>&1 | tee gui-bvt.log
	
	if [ `ruby ${CI_COMMON_HOME}/check_grid_errors.rb  "test-output/html"` != false ]; then
		echo_error "Got a Grid error."
		break
	fi

	ls test-output/junitreports/TEST-*.xml > /dev/null 2>&1
	if [ $? == 0 ]; then
		OK=true
		break
	fi

	echo_warn "Did not find a JUnit report file in ${CI_BVT_DIST_DIR}/test-output/junitreports"
	send_mail
	
	NUM_ATTEMPTS=`expr ${NUM_ATTEMPTS} + 1`
	
	if [ ${NUM_ATTEMPTS} -lt ${MAX_ATTEMPTS} ]; then
		echo_warn "Rerunning the pre-bvt (GUI) tests..."
	fi
done

if [ ${OK} != true ]; then
	echo_error "There were errors trying to execute the prebvt (GUI) tests."
	${CI_COMMON_HOME}/gen_workitem_params.sh assign_to_automation
	exit 1
fi

# Get back to workspace directory
cd -

LOCAL_GUI_RESULTS_DIR=prebvt-gui/results
rm -rf ${LOCAL_GUI_RESULTS_DIR}
mkdir -p ${LOCAL_GUI_RESULTS_DIR}

TESTNG_RESULTS_FILE_NAME=testng-results.xml
echo_info "Copying ${CI_BVT_DIST_DIR}/test-output/${TESTNG_RESULTS_FILE_NAME} to ${LOCAL_GUI_RESULTS_DIR}..."
cp ${CI_BVT_DIST_DIR}/test-output/${TESTNG_RESULTS_FILE_NAME} ${LOCAL_GUI_RESULTS_DIR}/${TESTNG_RESULTS_FILE_NAME}
STATUS=$?

# Capture the WAS login credentials and SystemOut logs.
${CI_COMMON_HOME}/get-was-info.sh

if [ ${STATUS} != 0 ]; then
	${CI_COMMON_HOME}/gen_workitem_params.sh assign_to_automation
	exit 1
fi

exit 0
