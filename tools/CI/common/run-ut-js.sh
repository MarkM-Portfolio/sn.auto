#!/bin/sh

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh assign_to_automation

echo "`date +"%F %T,%3N"` [CI-CD] INFO Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh
if [ $? != 0 ]; then
	echo "`date +"%F %T,%3N"` [CI-CD] ERROR Failed to load ${CI_COMMON_HOME}/ci_functions.sh."
	exit 1
fi

fail_with_error() {
    echo_error "$@"
	${CI_COMMON_HOME}/get-was-info.sh
    exit 1
}

. ${CI_COMMON_HOME}/system.properties || fail_with_error "Failed to load ${CI_COMMON_HOME}/system.properties."

COMPONENT=${1:-${BUILD_COMPONENT}}

index=1
case ${COMPONENT} in

	Blogs)
		TEST_MODULE[${index}]="lconn.blogs.test.specSuite"
		TEST_RUNNER[${index}]="lconn.blogs.test"
		RESULTS_XML_FILE[${index}]="TEST-lconn.blogs.test.specSuite.xml"
		index=`expr ${index} + 1`				
		;;

	Contacts)
		TEST_MODULE[${index}]="ic-contacts-test.specSuite"
		TEST_RUNNER[${index}]="ic-contacts-test"
		RESULTS_XML_FILE[${index}]="TEST-ic-contacts-test.specSuite.xml"
		index=`expr ${index} + 1`				
		;;

	Communities)
		TEST_MODULE[${index}]="lconn.comm.test.specSuite"
		TEST_RUNNER[${index}]="lconn.comm.test"
		RESULTS_XML_FILE[${index}]="TEST-lconn.comm.test.specSuite.xml"
		index=`expr ${index} + 1`				
		;;
	
	Files)
        #TEST_MODULE[${index}]="lconn.files.test.specSuite"
        #TEST_RUNNER[${index}]="lconn.test/jasmine"
        #RESULTS_XML_FILE[${index}]="TEST-lconn.files.test.specSuite.xml"
        #index=`expr ${index} + 1`

        TEST_MODULE[${index}]="ic-files-test.specSuite"
        TEST_RUNNER[${index}]="ic-test"
        RESULTS_XML_FILE[${index}]="TEST-ic-files-test.specSuite.xml"
        index=`expr ${index} + 1`
        ;;

	Forums)
		TEST_MODULE[${index}]="lconn.forums.test.specSuite"
		TEST_RUNNER[${index}]="lconn.forums.test"
		RESULTS_XML_FILE[${index}]="TEST-lconn.forums.test.specSuite.xml"
		index=`expr ${index} + 1`		
		;;
	
	Profiles)
		TEST_MODULE[${index}]="lconn.profiles.test.specSuite"
		TEST_RUNNER[${index}]="lconn.profiles.test"
		RESULTS_XML_FILE[${index}]="TEST-lconn.profiles.test.specSuite.xml"
		index=`expr ${index} + 1`		
		;;
	
	UI)
		# Legacy tests
		TEST_MODULE[${index}]="lconn.test.jasmine.specSuite"
		TEST_RUNNER[${index}]="lconn.test/jasmine"
		RESULTS_XML_FILE[${index}]="TEST-lconn.test.jasmine.specSuite.xml"
		index=`expr ${index} + 1`		

		TEST_MODULE[${index}]="com.ibm.oneui.test.jasmine.specSuite"
		TEST_RUNNER[${index}]="lconn.test/jasmine"
		RESULTS_XML_FILE[${index}]="TEST-com.ibm.oneui.test.jasmine.specSuite.xml"
		index=`expr ${index} + 1`		

		TEST_MODULE[${index}]="com.ibm.social.test.specSuite"
		TEST_RUNNER[${index}]="lconn.test/jasmine"
		RESULTS_XML_FILE[${index}]="TEST-com.ibm.social.test.specSuite.xml"
		index=`expr ${index} + 1`		
		
		TEST_MODULE[${index}]="lconn.share.test.specSuite"
		TEST_RUNNER[${index}]="lconn.test/jasmine"
		RESULTS_XML_FILE[${index}]="TEST-lconn.share.test.specSuite.xml"
		index=`expr ${index} + 1`		

		TEST_MODULE[${index}]="lconn.search.test.specSuite"
		TEST_RUNNER[${index}]="lconn.test/jasmine"
		RESULTS_XML_FILE[${index}]="TEST-lconn.search.test.specSuite.xml"
		index=`expr ${index} + 1`

		# AMD tests
		TEST_MODULE[${index}]="ic-test.specSuite"
		TEST_RUNNER[${index}]="ic-test"
		RESULTS_XML_FILE[${index}]="TEST-ic-test.specSuite.xml"
		index=`expr ${index} + 1`		
		
		TEST_MODULE[${index}]="ic-share-test.specSuite"
		TEST_RUNNER[${index}]="ic-test"
		RESULTS_XML_FILE[${index}]="TEST-ic-share-test.specSuite.xml"
		index=`expr ${index} + 1`		

		TEST_MODULE[${index}]="ic-social-test.specSuite"
		TEST_RUNNER[${index}]="ic-social-test"
		RESULTS_XML_FILE[${index}]="TEST-ic-social-test.specSuite.xml"
		index=`expr ${index} + 1`		

		TEST_MODULE[${index}]="ic-oauth-test.specSuite"
		TEST_RUNNER[${index}]="ic-test"
		RESULTS_XML_FILE[${index}]="TEST-ic-oauth-test.specSuite.xml"
		index=`expr ${index} + 1`		

		TEST_MODULE[${index}]="ic-search-test.specSuite"
		TEST_RUNNER[${index}]="ic-test"
		RESULTS_XML_FILE[${index}]="TEST-ic-search-test.specSuite.xml"
		index=`expr ${index} + 1`		
		;;
	
	WidgetsClib)
		TEST_MODULE[${index}]="quickr.lw.tests.jenkinsModule"
		TEST_RUNNER[${index}]="lconn.test/jasmine"
		RESULTS_XML_FILE[${index}]="TEST-quickr.lw.tests.xml"
		index=`expr ${index} + 1`		
		
		TEST_MODULE[${index}]="lconn.gallery.tests.jenkinsModule"
		TEST_RUNNER[${index}]="lconn.test/jasmine"
		RESULTS_XML_FILE[${index}]="TEST-lconn.gallery.tests.xml"
		index=`expr ${index} + 1`		

		TEST_MODULE[${index}]="ic-gallery-tests/module"
		TEST_RUNNER[${index}]="lconn.test/jasmine"
		RESULTS_XML_FILE[${index}]="TEST-ic-gallery-tests.xml"
		index=`expr ${index} + 1`		
		;;

	Wikis)
		TEST_MODULE[${index}]="lconn.wikis.test.specSuite"
		TEST_RUNNER[${index}]="lconn.wikis.test"
		RESULTS_XML_FILE[${index}]="TEST-lconn.wikis.test.specSuite.xml"
		index=`expr ${index} + 1`		
		;;

	*)	fail_with_error "Unknown component: ${COMPONENT}"
		;;
esac

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh

# Remove any old results files
rm -fv TEST-*.xml

# Run the tests
RET=true
NUM_TEST_MODULES=${#TEST_MODULE[@]}
for i in `seq 1 ${NUM_TEST_MODULES}`
do
	JASMINE_URL[i]="http://${WAS_HOST_FQDN}/connections/resources/web/${TEST_RUNNER[i]}/runner.html?render=test&testModule=${TEST_MODULE[i]}&debug=dojo"
	
	echo_info "Running ${CI_COMMON_HOME}/phantomjs ${CI_COMMON_HOME}/run-jasmine.js ${JASMINE_URL[i]} ${RESULTS_XML_FILE[i]}"
	${CI_COMMON_HOME}/phantomjs ${CI_COMMON_HOME}/run-jasmine.js "${JASMINE_URL[i]}" "${RESULTS_XML_FILE[i]}"

	echo_info "Checking for XML results file: ${RESULTS_XML_FILE[i]}..."
	if [ ! -f "${RESULTS_XML_FILE[i]}" ]; then
		echo_error "Could not find XML results file: ${RESULTS_XML_FILE[i]}"
		RET=false
	fi
done

[ ${RET} == true ] || fail_with_error "There were one or more missing XML results files."

# Capture the WAS login credentials and SystemOut logs.
${CI_COMMON_HOME}/get-was-info.sh

exit 0
