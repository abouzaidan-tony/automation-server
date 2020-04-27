#!/bin/bash

. ./setenv.sh

pid=$(./check.sh)
if [ "$pid" == "0" ]
then
    echo "Starting $NAME"
    java -Dname=$NAME -cp $CPATH $MAINCLASS > /dev/null 2>&1 &
else
    echo "Another instance of $NAME is running"
fi