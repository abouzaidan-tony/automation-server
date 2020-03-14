#!/bin/bash
. ./setenv.sh

pid=$(ps aux | grep Dname=$1  | grep -v grep | awk {'print $2'})
if [ -z "$pid" ]
then
    echo "0"
else
    echo "$pid"
fi
