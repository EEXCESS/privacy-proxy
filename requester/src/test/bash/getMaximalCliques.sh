#!/bin/bash

source ./common.sh

if [ "$#" -eq 2 ]; then 
	ServiceURL=`extractUrl $1`
	ServiceURL=`echo $ServiceURL"getMaximalCliques"`
	Method=$2
	curl -X $Method $ServiceURL
else 
	echo "Usage 'getMaximalCliques local-eclipse|local|remote-dev|remote-dev-test|<http://your-server/ GET'"
fi
