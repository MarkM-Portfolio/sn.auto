#!/bin/sh

SKIP_PREBVT_FILE=/local/ci/common/SKIP_PREBVT

echo "Creating ${SKIP_PREBVT_FILE}..."
echo "The existence of this file disables CI pre-BVT." > /local/ci/common/SKIP_PREBVT
