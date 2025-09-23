#!/bin/sh

TEST_CMD[1]="eval flock -x -w 900 /local/home/lcuser/emma/flock.lock -c 'ls -l ${HOME}'"
echo "TEST_CMD[1]: ${TEST_CMD[1]}"

${TEST_CMD[1]}

