#!/bin/sh

# this file suppose to be sourced in to bring functions to the other scripts
# the benifit of sourced functions is that we don't have to export a lot of
# environment varibles, and also they can be used in the ci.properties files

if [ -z "${BUILD_LABEL}" ]; then
	echo "\${BUILD_LABEL} is null."
	exit 1
fi

export BUILD_STREAM=`echo ${BUILD_LABEL} | sed 's/\(.*\)_/\1:/' | cut -d ':' -f1 | sed 's/\(.*\)_/\1:/' | cut -d ':' -f1`
export BUILD_COMPONENT=`echo ${BUILD_LABEL} | sed 's/\(.*\)_/\1:/' | cut -d ':' -f1 | sed 's/\(.*\)_/\1:/' | cut -d ':' -f2`

echo "BUILD_STREAM: ${BUILD_STREAM}"
echo "BUILD_COMPONENT: ${BUILD_COMPONENT}"

# Extract jar file.
# Replace the database.properties file.
# Package up jar.
# Replace original jar with newly packaged up jar.
replace_database_properties_jar() {
    set -x
    #jars=`find "${CI_HOME}/${SRC}/" -name "home.news.test.jar"`
    local jar_file=$1
    pwd
    mkdir -p test/spring/home-news/database
    cp ${CI_HOME}/database.properties test/spring/home-news/database/database.properties
    jar uvf "${jar_file}" test/spring/home-news/database/database.properties
    set +x
}

is_grid_running() {
	for i in {1..10}; do
		echo "Checking grid..."
		code=`curl -o /dev/null -m 5 --silent --head --write-out '%{http_code}\n' http://lcrft04.cnx.cwp.pnp-hcl.com:5555/grid/api/hub`
		if [ $code -eq 200 ]; then
			echo "Grid is running"
			return 0
		fi
		echo "Grid not running; will retry in 1 minute."
		sleep 60
	done

	echo "Grid still not running, giving up..."
	return 1
}

echo_with_timestamp() {
        echo -e  "`date +"%F %T,%3N"` [CI-CD] $1"
}

echo_info() {
        echo_with_timestamp "INFO  $1"
}

echo_error() {
        echo_with_timestamp "ERROR  $1"
}

echo_warn() {
        echo_with_timestamp "WARN  $1"
}
