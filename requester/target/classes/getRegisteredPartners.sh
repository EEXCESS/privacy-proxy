#!/bin/bash

source ./common.sh

if [ "$#" -eq 2 ]; then 
	ServiceURL=`extractUrl $1`
	ServiceURL=`echo $ServiceURL"getRegisteredPartners"`
	Method=$2
	echo $ServiceURL
	curl -X $Method $ServiceURL 
else 
	echo "Usage 'getRegisteredPartners local-eclipse|local|remote-dev|remote-dev-test|<http://your-server/ GET'"
fi
