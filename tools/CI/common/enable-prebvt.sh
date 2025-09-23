#!/bin/sh

SKIP_PREBVT_FILE=/local/ci/common/SKIP_PREBVT

echo "Removing ${SKIP_PREBVT_FILE}..."
rm /local/ci/common/SKIP_PREBVT
if [ $? != 0 ]; then
	echo "Could not remove ${SKIP_PREBVT_FILE}."
fi

