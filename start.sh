#!/bin/bash
MAINCLASS=com.tony.automationserver.Server
CPATH=target/*:target/dependency/*:config
java -cp $CPATH $MAINCLASS
