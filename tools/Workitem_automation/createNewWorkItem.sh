JSONPATH="../../wink-json4j/wink-json4j-1.1.3-incubating.jar"
RTC_API_PATH="../RTC_API/"
VERSION="$1"

usage() {
    echo "Usage: createNewWorkItem.sh <version>"
    exit 1
}

if [ -z $VERSION ]; then
    echo "ERROR:"
    echo " You need to specify build version"
    usage
fi

jython -Dpython.path=$JSONPATH:$RTC_API_PATH ./createNewWorkItem.py $VERSION

stty sane