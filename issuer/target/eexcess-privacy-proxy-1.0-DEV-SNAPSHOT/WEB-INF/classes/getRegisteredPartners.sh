#!/bin/bash

source ./common.sh

if [ "$#" -eq 2 ]; then 
	ServiceURL=`extractUrl $1`
	ServiceURL=`echo $ServiceURL"api/v1/getRegisteredPartners"`
	Method=$2
	curl -X $Method $ServiceURL 
else 
	echo "Usage 'getRegisteredPartners local-eclipse|local|remote-dev|remote-dev-test|<http://your-server/ GET'"
fi
