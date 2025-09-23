#!/bin/sh

JENKINS_UID=C-CF8L
JENKINS_PASSWORD=`cat /local/ci/common/.icci`
JENKINS_URL=http://jenkinspoc.cnx.cwp.pnp-hcl.com/jenkins
JENKINS_TOKEN=YWN0aXZpdG11cwo

BUILD_HOME=/net/mlsa2/ibm/releng/workplace/dailybuilds


# trigger_jenkins_job job_name label
#
#   trigger the given jenkins job with parameter BUILD_LABEL set to give label
trigger_jenkins_job() {
    local job=$1
    local label=$2
    local json="{\"parameter\": [{\"name\": \"BUILD_LABEL\", \"value\": \"$label\"}], \"\": \"\"}"
    curl --fail -X POST $JENKINS_URL/job/${job}/build \
         --user "${JENKINS_UID}:${JENKINS_PASSWORD}" \
         -d token=${JENKINS_TOKEN} --data-urlencode json="$json"
}


#
# monitor_build_and_trigger_jenkins STREAM PROJECT [Jenkins_Job]
#
# example:
#   monitor_build_and_trigger_jenkins IC10.0 Activities
#   monitor_build_and_trigger_jenkins IC10.0 Activities Activities_GUI
#
monitor_build_and_trigger_jenkins() {
    local stream=$1
    local project=$2
    local jenkins_job=$3
    jenkins_job=${jenkins_job:-${project}}
    local bvt_label_file=${BUILD_HOME}/${stream}_${project}/BVTBuildLabel.txt
    local current_label_file=${BUILD_HOME}/${stream}_${project}/currentBuildLabel.txt
    local last_tested_label_file=/tmp/${stream}_${project}_TestedLabel.txt

    echo "***************************************************************"
    echo "  Checking build for $stream - $project"
    echo "***************************************************************"

    for f in $current_label_file ; do
        if [ ! -f "${f}" ]; then
            echo "ERROR - unable to find file ${f}"
            return 1
        fi
    done

    local current_label=`cat ${current_label_file} | tr -d '\n\r'`
    local bvt_label=""
    if [ -f "${bvt_label_file}" ] ; then
        bvt_label=`cat ${bvt_label_file} | tr -d '\n\r'`
    fi
    local last_tested_label=""

    if [ -f ${last_tested_label_file} ] ; then
        last_tested_label=`cat ${last_tested_label_file}`
    fi

    echo "Current Build is: [${current_label}]"
    echo "Last BVT Pass Build is: [${bvt_label}]"
    echo "Last Tested Build is: [${last_tested_label}]"

    if [ -z "${current_label}" ]; then
        echo "ERROR - current build lable is empty, bug???"
        return 1
    fi

    if [ 1 = `expr ${last_tested_label:-${stream}} \< ${current_label}` ] ; then
		echo "Found new build ${current_label}; checking to see if it is fully mastered..."
		if [ ! -f "${BUILD_HOME}/${stream}_${project}/${current_label}/MASTERED.sem" ]; then
			echo "${current_label} is not fully mastered yet." | tee -a /tmp/djm.log
			return 0
		fi

        echo "Build [${current_label}] needs to be  tested, submit to Jenkins."
        if trigger_jenkins_job ${jenkins_job} ${current_label} ; then
            echo "  Jenkins successfully triggered, record [${current_label}] to be tested"
            echo -n "${current_label}" > "${last_tested_label_file}"
        fi
    fi
}


STREAMS="IC10.0"
#PROJECTS=""

#for stream in ${STREAMS}; do
#    for project in ${PROJECTS}; do
#        monitor_build_and_trigger_jenkins ${stream} ${project} ${stream}_${project}_UT
#    done
#done

PROJECTS="Activities Blogs Bookmarks Communities Forums Homepage Infra Metrics Mobile \
ExtensionsRegistry \
Moderation News PlacesCatalog Profiles RichTextEditors Search WidgetsCal Share UI WidgetsClib"

for stream in ${STREAMS}; do
    for project in ${PROJECTS}; do
        monitor_build_and_trigger_jenkins ${stream} ${project} ${stream}_${project}
    done
done


#monitor_build_and_trigger_jenkins "IC10.0" "Install" "Deploy_lc30linux3"
monitor_build_and_trigger_jenkins "IC10.0" "Connections" "IC10.0_Connections"
monitor_build_and_trigger_jenkins "IC20.0" "Connections" "IC20.0_Connections"
monitor_build_and_trigger_jenkins "IC30.0" "Connections" "IC30.0_Connections"
monitor_build_and_trigger_jenkins "IC40.0" "Connections" "IC40.0_Connections"
monitor_build_and_trigger_jenkins "IC40.0P" "Connections" "IC40.0P_Connections"
monitor_build_and_trigger_jenkins "IC20.0" "Activities" "IC20.0_Activities"
monitor_build_and_trigger_jenkins "IC5.0_SUPPORT" "Connections" "IC5.0_SUPPORT_Connections"
monitor_build_and_trigger_jenkins "IC5.0_CR" "Connections" "IC5.0_CR_Connections"
monitor_build_and_trigger_jenkins "IC5.5" "Connections" "IC5.5_Connections"

STREAMS="IC10.0 IC20.0 IC30.0 IC40.0 IC5.0_SUPPORT IC5.0_CR IC5.5"
PROJECTS="Automation"

for stream in ${STREAMS}; do
    for project in ${PROJECTS}; do
        monitor_build_and_trigger_jenkins ${stream} ${project} ${stream}_${project}
    done
done

