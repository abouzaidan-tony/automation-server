#!/bin/bash
MAINCLASS=com.tony.automationserver.Server
CPATH=target/*:target/dependency/*:src/main/resources
java -cp $CPATH $MAINCLASS
