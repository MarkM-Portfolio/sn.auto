#!/bin/sh

echo "Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/ci_functions.sh."
	exit 1
fi

. ${CI_COMMON_HOME}/system.properties
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/system.properties."
	exit 1
fi
	
# Initialize the Mantis build environment.
if [ ! -d lwp04.tools ]; then
	ln -s ${CI_COMMON_HOME}/lwp04.tools lwp04.tools
fi

cat <<EOF > wplclocal.sh
export APPLICATION_DIRNAME
export APPDIR
export APPDIR_PRIMARY=${APPDIR}
export JAVA50_ROOT=${JAVA50_ROOT:-/local/ci/common/ibm-java2-i386-50}
export JAVA60_ROOT=${JAVA60_ROOT:-/local/ci/common/ibm-java-x86_64-60}
export ECLIPSE_HOME=${ECLIPSE_HOME:-/local/ci/common/eclipse}
export ECLIPSE34_HOME=${ECLIPSE34_HOME:-/local/ci/common/eclipse34}
export BINSDAILY=${BINSDAILY:-/net/mlsa2/ibm/releng/workplace/dailykits}
export MSTRDAILY=${MSTRDAILY:-/net/mlsa2/ibm/releng/workplace/dailybuilds/}
export FE_DOWNLOAD_DIR="\$BINSDAILY,\$MSTRDAILY"
EOF
chmod +x wplclocal.sh

export BUILD_HOME="${BUILD_HOME:-$PWD}"
. ./lwp04.tools/lwp/boot.sh
