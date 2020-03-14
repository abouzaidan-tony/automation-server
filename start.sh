#!/bin/bash

. ./setenv.sh

pid=$(./check.sh)
if [ "$?" == "0" ]
then
    echo "Starting $NAME"
    java -Dname=$NAME -cp $CPATH $MAINCLASS &
else
    echo "Another instance of $NAME is running"
fi